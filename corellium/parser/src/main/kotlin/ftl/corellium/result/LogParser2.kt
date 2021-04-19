@file:Suppress("EXPERIMENTAL_API_USAGE")

package ftl.corellium.result

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform

// without summary part
suspend fun Flow<String>.parseLogs2(): List<TestResult2> = filterNot(String::isBlank)
    .applyCustomFilters()
    .toSingleLines()
    .map { it.substringAfter("INSTRUMENTATION_STATUS:").trim() }
    .map { it.replace("INSTRUMENTATION_STATUS_CODE:\\s".toRegex(), "code=") }
    .map { it.split("=") }
    .filterNot { it[0] == "stack" }
    .map { splittedLine -> Lines2.valueOf(splittedLine[0].toUpperCase()) to splittedLine[1] }
    .toChunks()
    .map { map ->
        TestResult2(
            clazz = map.getValue(Lines2.CLASS),
            current = map.getValue(Lines2.CURRENT).toInt(),
            id = map.getValue(Lines2.ID),
            numTests = map.getValue(Lines2.NUMTESTS).toInt(),
            test = map.getValue(Lines2.TEST),
            stream = map.getValue(Lines2.STREAM).lines().flatMap { it.split('\t') }.map { it.trim() },
            code = map.getValue(Lines2.CODE).toInt(),
        )
    }
    .toList()

// we need to filter some pollution from corellium
private fun Flow<String>.applyCustomFilters() = filterNot { line -> filters.any { it(line) } }

private val filters: List<(String) -> Boolean> = listOf(
    { it.contains("adb shell am instrument") }
)

private suspend fun Flow<String>.toSingleLines() = coroutineScope {
    val builder = StringBuilder()
    transform { line ->
        if (line.startsWith("INSTRUMENTATION_STATUS") || line.startsWith("INSTRUMENTATION_RESULT")) {
            emit(builder.toString())
            builder.clear()
        }
        builder.append(line)
    }.filterNot { it.isEmpty() }
}

private suspend fun Flow<Pair<Lines2, String>>.toChunks(): Flow<Map<Lines2, String>> = coroutineScope {
    val valueMap = mutableMapOf<Lines2, String>()
    transform { (line, value) ->
        if (valueMap.keys.containsAll(Lines2.values().toList())) {
            emit(valueMap.toMap())
            valueMap.clear()
        }
        valueMap[line] = value
    }
}

private enum class Lines2 {
    CLASS, CURRENT, ID, NUMTESTS, STREAM, CODE, TEST; // no stack
}

data class TestResult2(
    val clazz: String,
    val current: Int,
    val id: String,
    val numTests: Int,
    val test: String,
    val stream: List<String>,
    val code: Int = Int.MIN_VALUE,
) {
    val timestamp: Long = System.currentTimeMillis()
}
