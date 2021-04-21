package flank.corellium.logs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform

// Stage 1 ============================================
/**
 * Group instrumentation output logs
 * @receiver Flow of lines
 * @return Flow of groups where the last line always contains status code
 */
internal fun Flow<String>.groupLines(): Flow<List<Line>> {
    val accumulator = mutableListOf<Line>()
    return transform { line ->
        val (prefix, text) = line.parsePrefix()
        accumulator += Line(prefix, text)
        when (prefix) {
            Type.StatusCode.text,
            Type.Code.text -> {
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
internal data class Line(
    val prefix: String?,
    val text: String,
)

private fun String.parsePrefix(): Pair<String?, String> {
    val prefix = Type.values().firstOrNull { startsWith(it.text) }?.text
    return prefix to (prefix?.let { removePrefix(it) } ?: this)
}

private enum class Type(val text: String) {
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
internal fun Flow<List<Line>>.parseChunks(): Flow<Chunk> = map { group ->
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
internal data class Chunk(
    val type: String,
    val code: Int,
    val map: Map<String, List<String>>,
    val timestamp: Long = System.currentTimeMillis(),
)

// Stage 3 ============================================

internal fun Flow<Chunk>.parseStatusResult(): Flow<Instrument> {
    var prev = Chunk(
        type = "",
        code = 0,
        map = mapOf("current" to listOf("0"))
    )

    return transform { next ->
        when (next.type) {

            // Handling the regular chunk which is representing the half of Status.
            Type.StatusCode.text -> when {
                prev.id == next.id -> emit(createStatus(prev, next))
                prev.id < next.id -> prev = next
                else -> throw IllegalArgumentException("Inconsistent stream of chunks.\nexpected pair for: $prev\nbut was $next")
            }

            // Handling the final chunk which is representing the Result.
            Type.Code.text -> emit(createResult(next))

            else -> throw IllegalArgumentException("Unknown type of Chunk: ${next.type}")
        }
    }
}

private val Chunk.id: Int get() = map.getValue("current").first().toInt()

private fun createStatus(first: Chunk, second: Chunk) = Instrument.Status(
    code = second.code,
    startTime = first.timestamp,
    endTime = second.timestamp,
    details = (first.map + second.map).mapValues { (key, value) ->
        when (key) {
            "id",
            "test",
            "class",
            -> value.first()

            "current",
            "numTests",
            -> value.first().toInt()

            else -> value
        }
    }
)

private fun createResult(chunk: Chunk) = Instrument.Result(
    code = chunk.code,
    details = chunk.map,
    time = chunk.timestamp
)
