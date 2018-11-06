package ftl.json

import ftl.util.MatrixState

class MatrixMap(
    val map: MutableMap<String, SavedMatrix>,
    val runPath: String
) {
    // determine success by FTL API, not junit xml.
    // FTL sometimes generates empty XML reports (0 failures) for a failed matrix
    fun allSuccessful(): Boolean {
        val savedMatrices = map.values
        var successful = true

        savedMatrices.forEach { matrix ->
            if (matrix.failed()) successful = false
        }

        return successful
    }

    /**
     * There are two sources of information for detecting the exit code
     * 1) Matrix state via test API (MatrixState.kt)
     * 2) Step outcome via tool results API (Outcome.kt)
     *
     * A test that fails will have a matrix state of finished with an outcome of failure.
     * A matrix state of error means FTL had an infrastructure failure
     * A step outcome of failure means at least one test failed.
     *
     * exit code 0 - all tests passed
     * exit code 1 - at least one test failed/inconclusive & all matrices finished
     * exit code 2 - at least one matrix not finished (usually FTL error)
     */
    fun exitCode(): Int {
        var exitCode = 0

        map.values.forEach { matrix ->
            if (matrix.state != MatrixState.FINISHED) return 2
            if (matrix.failed()) exitCode = 1
        }

        return exitCode
    }
}
