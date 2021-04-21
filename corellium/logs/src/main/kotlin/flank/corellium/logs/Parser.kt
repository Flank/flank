package flank.corellium.logs

import kotlinx.coroutines.flow.Flow

/**
 * Parse instrument test output logs into structures.
 * This parser requires a clean logs only from the single command execution.
 * @receiver The [Flow] of [String] lines from console output produced by "am instrument -r -w" command.
 * @return The [Flow] of [Instrument] structures. Only the last element of flow is [Instrument.Result]
 */
fun Flow<String>.parseAdbInstrumentLog(): Flow<Instrument> = this
    .groupLines()
    .parseChunks()
    .parseStatusResult()


sealed class Instrument {
    /**
     * Representation of the pair of status chunks.
     */
    class Status(
        val code: Int,
        val startTime: Long,
        val endTime: Long,
        val details: Map<String, Any>
    ) : Instrument()

    /**
     * Representation of the final structure of instrument test logs.
     */
    class Result(
        val code: Int,
        val time: Long,
        val details: Map<String, Any>
    ) : Instrument()
}
