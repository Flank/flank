package flank.instrument.log

import kotlinx.coroutines.flow.Flow

/**
 * Parse instrument test logs into structures.
 * This parser requires a clean logs only from the single command execution.
 *
 * @receiver The [Flow] of [String] lines from console output produced by "am instrument -r -w" command.
 * @return The [Flow] of [Instrument] structures. Only the last element of flow is [Instrument.Result]
 */
fun Flow<String>.parseAdbInstrumentLog(): Flow<Instrument> = this
    .groupLines()
    .parseChunks()
    .parseStatusResult()

sealed class Instrument {
    /**
     * Representation of the following pair of two status chunks:
     * ```
     * INSTRUMENTATION_STATUS: class=com.example.test_app.InstrumentedTest
     * INSTRUMENTATION_STATUS: current=1
     * INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
     * INSTRUMENTATION_STATUS: numtests=3
     * INSTRUMENTATION_STATUS: stream=
     * com.example.test_app.InstrumentedTest:
     * INSTRUMENTATION_STATUS: test=ignoredTestWithIgnore
     * INSTRUMENTATION_STATUS_CODE: 1
     * INSTRUMENTATION_STATUS: class=com.example.test_app.InstrumentedTest
     * INSTRUMENTATION_STATUS: current=1
     * INSTRUMENTATION_STATUS: id=AndroidJUnitRunner
     * INSTRUMENTATION_STATUS: numtests=3
     * INSTRUMENTATION_STATUS: stream=
     * com.example.test_app.InstrumentedTest:
     * INSTRUMENTATION_STATUS: test=ignoredTestWithIgnore
     * INSTRUMENTATION_STATUS_CODE: -3
     * ```
     *
     * @property code The value of INSTRUMENTATION_STATUS_CODE of the second chunk
     * @property startTime The time of creation the first chunk of status.
     * @property endTime The time of creation the second chunk of status.
     * @property details The summary details of both chunks.
     */
    class Status(
        val code: Int,
        val startTime: Long,
        val endTime: Long,
        val details: Details,
    ) : Instrument() {

        class Details(
            val raw: Map<String, Any>,
            val className: String,
            val testName: String,
            val stack: String?,
        )
    }

    /**
     * Representation of the final structure of instrument test logs:
     *
     * ```
     * INSTRUMENTATION_RESULT: stream=
     *
     * Time: 2.076
     *
     * OK (2 test)
     *
     *
     * INSTRUMENTATION_CODE: -1
     * ```
     *
     * @property code The value of INSTRUMENTATION_CODE
     * @property time The time of creation the result chunk.
     * @property details The details recorded for the result (Perhaps only a "stream" value).
     */
    class Result(
        val code: Int,
        val time: Long,
        val details: Map<String, Any>
    ) : Instrument()

    /**
     * Status code from am instrument output
     *
     * @property RUNNING Test running
     * @property PASSED Test passed
     * @property FAILED Assertion failure
     * @property EXCEPTION Other exception
     */
    object Code {
        const val RUNNING = 1
        const val PASSED = 0
        const val FAILED = -2
        const val EXCEPTION = -1
        const val SKIPPED = -3
    }
}
