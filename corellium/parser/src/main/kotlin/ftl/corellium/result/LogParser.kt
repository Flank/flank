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
                line isLine "class" -> emitAndCreateNew(line)
                line isLine "result" -> emitAndCreateSummary(line)
                line isLine "run-code" -> emit(logChunk.updateLogs(line))
                else -> logChunk = logChunk.updateLogs(line)
            }
        }
}

private fun LogsChunk.toTestResult(): TestResult {
    var result = TestResult()
    logs.forEach { log ->
        result = when {
            log isLine "class" -> result.copy(clazz = log.getValue("class"))
            log isLine "numtests" -> result.copy(numTests = log.getValue("numtests").toInt())
            log isLine "id" -> result.copy(id = log.getValue("id"))
            log isLine "current" -> result.copy(current = log.getValue("current").toInt())
            log isLine "code" -> result.copy(code = log.getValue("code").toInt())
            log isLine "stack" -> result.copy(stack = listOf(log.getValue("stack")))
            result.stack.isNotEmpty() -> result.copy(stack = result.stack + log)
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
            log isLine "result" -> TestSummary()
            log isLine "time" -> summary.copy(time = log.getValue("time"))
            log isLine "run-code" -> summary.copy(
                code = log.getValue("run-code").toInt(),
                errors = summary.errors + if (error.stack.isNotEmpty()) listOf(error) else emptyList()
            )
            log isLine "error-summary" -> summary
            log isLine "ok" -> summary
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

private fun LogsChunk.updateLogs(line: String) = copy(logs = logs + line)
private fun TestError.getOrEmpty() = if (stack.isNotEmpty()) listOf(this) else emptyList()
private val testFailure = "[0-9]*\\)\\s(.*)\\((.*)\\)".toRegex()
private fun String.getValue(line: String) = substringAfter(lines[line] ?: error("Should not happen"))
private infix fun String.isLine(line: String) = contains(lines[line] ?: error("Should not happen"))
private infix fun String.isLine(regex: Regex) = contains(regex)

private val lines = listOf(
    "numtests",
    "stack",
    "id",
    "current",
    "test",
    "class"
).associateWith { "INSTRUMENTATION_STATUS: $it=" } + mapOf(
    "code" to "INSTRUMENTATION_STATUS_CODE: ",
    "result" to "INSTRUMENTATION_RESULT",
    "time" to "Time: ",
    "failures" to "There were",
    "run-code" to "INSTRUMENTATION_CODE: ",
    "error-summary" to "There were",
    "ok" to "OK ("
)

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
    val stream: List<String> = emptyList(),
    val stack: List<String> = emptyList(),
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
