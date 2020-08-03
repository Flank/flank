package ftl.json

import com.google.api.services.testing.model.TestMatrix
import ftl.util.FTLError
import ftl.util.FailedMatrix
import ftl.util.IncompatibleTestDimension
import ftl.util.InfrastructureError
import ftl.util.MatrixCanceled
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
     * @throws MatrixCanceled [MatrixCanceled]
     *         at least one matrix canceled by user
     * @throws InfrastructureError [InfrastructureError]
     *         at least one matrix have a test infrastructure error
     * @throws IncompatibleTestDimension [IncompatibleTestDimension]
     *         at least one matrix have a incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
     * @throws FTLError [FTLError]
     *         at least one matrix have unexpected error
     */
    fun validateMatrices(shouldIgnore: Boolean = false) {
        map.values.run {
            firstOrNull { it.canceledByUser() }?.let { throw MatrixCanceled(it.outcomeDetails) }
            firstOrNull { it.infrastructureFail() }?.let { throw InfrastructureError(it.outcomeDetails) }
            firstOrNull { it.incompatibleFail() }?.let { throw IncompatibleTestDimension(it.outcomeDetails) }
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

fun Iterable<TestMatrix>.update(matrixMap: MatrixMap) = forEach { matrix ->
    matrixMap.map[matrix.testMatrixId]?.update(matrix)
}
