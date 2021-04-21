package flank.corellium.logs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

// Stage 1 ==================================
/**
 * Group instrumentation output logs
 * @receiver Flow of lines
 * @return Flow of groups where the last line always contains status code
 */
fun Flow<String>.groupLines(): Flow<List<Line>> {
    val accumulator = mutableListOf<Line>()
    return transform { line ->
        val (prefix, text) = line.parsePrefix()
        accumulator += Line(prefix, text)
        when (prefix) {
            Instrumentation.StatusCode.text,
            Instrumentation.Code.text -> {
                emit(accumulator.toList())
                accumulator.clear()
            }
        }
    }
}
/**
 * Parsed line of instrumentation output. For example:
 * ```
 * INSTRUMENTATION_STATUS: test=ignoredTestWithIgnore
 * ```
 * @property prefix `"INSTRUMENTATION_STATUS: "`
 * @property text `"test=ignoredTestWithIgnore"`
 */
data class Line(
    val prefix: String?,
    val text: String,
)
private fun String.parsePrefix(): Pair<String?, String> {
    val prefix = Instrumentation.values().firstOrNull { startsWith(it.text) }?.text
    return prefix to (prefix?.let { removePrefix(it) } ?: this)
}
private enum class Instrumentation(val text: String) {
    Status("INSTRUMENTATION_STATUS: "),
    StatusCode("INSTRUMENTATION_STATUS_CODE: "),
    Result("INSTRUMENTATION_RESULT: "),
    Code("INSTRUMENTATION_CODE: "),
}
// Stage 2 ============================================
/**
 * Parse previously grouped lines into chunks
 * @receiver [Flow] of [Line] groups
 * @return [Flow] of [Chunk]
 */
fun Flow<List<Line>>.parseChunks(): Flow<Chunk> = map { group ->
    val reversed = group.reversed().toMutableList()
    val code = reversed.removeFirst()
    val linesAccumulator = mutableListOf<String>()
    val map = mutableMapOf<String, List<String>>()
    reversed.forEach { line ->
        if (line.prefix == null)
            linesAccumulator += line.text
        else
            linesAccumulator.apply {
                val (key, text) = line.text.split("=", limit = 2)
                add(text)
                map[key] = reversed()
                clear()
            }
    }
    Chunk(code.prefix!!, code.text.toInt(), map)
}
/**
 * The structured representation of instrumentation output lines followed by result code.
 * @property type The prefix of status code line: [INSTRUMENTATION_STATUS_CODE | INSTRUMENTATION_CODE]
 * @property code The result code.
 * @property map The properties for the specific group of lines.
 */
data class Chunk(
    val type: String,
    val code: Int,
    val map: Map<String, List<String>>,
)
