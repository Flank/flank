package ftl.json

import ftl.util.FTLError
import ftl.util.FailedMatrix
import ftl.util.MatrixState

class MatrixMap(
    val map: Map<String, SavedMatrix>,
    val runPath: String
) {
    // determine success by FTL API, not junit xml.
    // FTL sometimes generates empty XML reports (0 failures) for a failed matrix
    fun allSuccessful(): Boolean = map.values.any(SavedMatrix::failed).not()

    /**
     * There are two sources of information for detecting the exit code
     * 1) Matrix state via test API (MatrixState.kt)
     * 2) Step outcome via tool results API (Outcome.kt)
     *
     * A test that fails will have a matrix state of finished with an outcome of failure.
     * A matrix state of error means FTL had an infrastructure failure
     * A step outcome of failure means at least one test failed.
     *
     * @param shouldIgnore [Boolean]
     *        set {true} to ignore failed matrices and exit with status code 0
     *
     * @throws FailedMatrix [FailedMatrix]
     *         at least one test failed/inconclusive & all matrices finished
     * @throws FTLError [FTLError]
     *         at least one matrix not finished (usually FTL error)
     */
    fun validateMatrices(shouldIgnore: Boolean = false) {
        map.values.run {
            firstOrNull { it.state != MatrixState.FINISHED }?.let { throw FTLError(it) }
            filter { it.failed() }.let {
                if (it.isNotEmpty()) throw FailedMatrix(
                    matrices = it,
                    ignoreFailed = shouldIgnore
                )
            }
        }
    }
}
