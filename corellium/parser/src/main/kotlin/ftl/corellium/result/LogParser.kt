@file:Suppress("EXPERIMENTAL_API_USAGE")

package ftl.corellium.result

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform

suspend fun Flow<String>.parseLogs(): List<AndroidRunLog> = toLogChunks()
    .filterNot { it.type == ChunkType.NONE }
    .map {
        when (it.type) {
            ChunkType.RESULT -> it.toTestResult()
            ChunkType.SUMMARY -> it.toTestSummary()
            else -> error("We should not have different chunk types")
        }
    }
    .toList()

private suspend fun Flow<String>.toLogChunks(): Flow<LogsChunk> = coroutineScope {
    var logChunk = LogsChunk()

    val emitAndCreateNew: suspend FlowCollector<LogsChunk>.(String) -> Unit = { line ->
        if (logChunk.logs.isNotEmpty()) {
            emit(logChunk)
        }
        logChunk = LogsChunk(type = ChunkType.RESULT, logs = listOf(line))
    }

    val emitAndCreateSummary: suspend FlowCollector<LogsChunk>.(String) -> Unit = { line ->
        if (logChunk.logs.isNotEmpty()) {
            emit(logChunk)
        }
        logChunk = LogsChunk(type = ChunkType.SUMMARY, logs = listOf(line))
    }

    filterNot(String::isBlank)
        .transform { line ->
            when {
                line isLine Lines.CLASS -> emitAndCreateNew(line)
                line isLine Lines.RESULT -> emitAndCreateSummary(line)
                line isLine Lines.TEST_RUN_CODE -> emit(logChunk.updateLogs(line))
                else -> logChunk = logChunk.updateLogs(line)
            }
        }
}

private fun LogsChunk.toTestResult(): TestResult {
    var result = TestResult()
    logs.forEach { log ->
        result = when {
            log isLine Lines.CLASS -> result.copy(clazz = log getValue Lines.CLASS)
            log isLine Lines.NUMTESTS -> result.copy(numTests = (log getValue Lines.NUMTESTS).toInt())
            log isLine Lines.ID -> result.copy(id = log getValue Lines.ID)
            log isLine Lines.CURRENT -> result.copy(current = (log getValue Lines.CURRENT).toInt())
            log isLine Lines.TEST_CODE -> result.copy(code = (log getValue Lines.TEST_CODE).toInt())
            log isLine Lines.STACK -> result.copy(stack = listOf(log getValue Lines.STACK))
            log isLine Lines.TEST -> result.copy(test = log getValue Lines.TEST)
            log isLine Lines.STREAM -> result.copy(updateStack = false)
            result.stack.isNotEmpty() && result.updateStack -> result.copy(stack = result.stack + log)
            else -> result
        }
    }
    return result
}

private fun LogsChunk.toTestSummary(): TestSummary {
    lateinit var summary: TestSummary
    var error = TestError()
    logs.forEach { log ->
        summary = when {
            log isLine Lines.RESULT -> TestSummary()
            log isLine Lines.TIME -> summary.copy(time = log getValue Lines.TIME)
            log isLine Lines.TEST_RUN_CODE -> summary.copy(
                code = (log getValue Lines.TEST_RUN_CODE).toInt(),
                errors = summary.errors + if (error.stack.isNotEmpty()) listOf(error) else emptyList()
            )
            log isLine Lines.FAILURES_SUMMARY -> summary
            log isLine Lines.TEST_RUN_OK -> summary
            log isLine testFailure -> {
                val previousError = error.getOrEmpty()
                error = testFailure.find(log)?.groupValues?.run { TestError(test = get(1), testClass = get(2)) }
                    ?: error("Should not happen")
                summary.copy(errors = summary.errors + previousError)
            }
            else -> {
                error = error.copy(stack = error.stack + log)
                summary
            }
        }
    }
    return summary
}

private fun LogsChunk.updateLogs(line: String) = copy(logs = logs + line.trim())
private fun TestError.getOrEmpty() = if (stack.isNotEmpty()) listOf(this) else emptyList()
private val testFailure = "[0-9]*\\)\\s(.*)\\((.*)\\)".toRegex()
private infix fun String.getValue(line: Lines) = substringAfter(line.fullLine).trim()
private infix fun String.isLine(line: Lines) = startsWith(line.fullLine)
private infix fun String.isLine(regex: Regex) = contains(regex)

private enum class Lines(
    val fullLine: String
) {
    NUMTESTS(makeFullLine("numtests")),
    STACK(makeFullLine("stack")),
    STREAM(makeFullLine("stream")),
    ID(makeFullLine("id")),
    CURRENT(makeFullLine("current")),
    TEST(makeFullLine("test")),
    CLASS(makeFullLine("class")),
    TEST_CODE("INSTRUMENTATION_STATUS_CODE:"),
    RESULT("INSTRUMENTATION_RESULT"),
    TIME("Time:"),
    FAILURES_SUMMARY("There were"),
    TEST_RUN_CODE("INSTRUMENTATION_CODE:"),
    TEST_RUN_OK("OK")
}

private fun makeFullLine(line: String) = "INSTRUMENTATION_STATUS: $line="

private enum class ChunkType {
    RESULT, SUMMARY, NONE
}

private data class LogsChunk(
    val type: ChunkType = ChunkType.NONE,
    val logs: List<String> = emptyList()
)

sealed class AndroidRunLog(
    open val code: Int
) {
    val timestamp: Long = System.currentTimeMillis()
}

data class TestResult(
    val clazz: String = "",
    val current: Int = Int.MIN_VALUE,
    val id: String = "",
    val numTests: Int = Int.MIN_VALUE,
    val test: String = "",
    val stack: List<String> = emptyList(),
    val updateStack: Boolean = true,
    override val code: Int = Int.MIN_VALUE,
) : AndroidRunLog(code)

data class TestSummary(
    override val code: Int = Int.MIN_VALUE,
    val time: String = "",
    val result: Map<String, Int> = emptyMap(),
    val errors: List<TestError> = emptyList()
) : AndroidRunLog(code)

data class TestError(
    val test: String = "",
    val testClass: String = "",
    val stack: List<String> = emptyList()
)
