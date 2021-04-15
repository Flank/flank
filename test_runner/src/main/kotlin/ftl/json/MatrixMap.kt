package ftl.json

import com.google.testing.model.TestMatrix
import ftl.adapter.environment.orUnknown
import ftl.reports.outcome.getOutcomeMessageByKey
import ftl.run.exception.FTLError
import ftl.run.exception.FailedMatrixError
import ftl.run.exception.IncompatibleTestDimensionError
import ftl.run.exception.InfrastructureError
import ftl.run.exception.MatrixCanceledError
import ftl.run.exception.MatrixValidationError
import ftl.util.MatrixState

data class MatrixMap(val map: Map<String, SavedMatrix>, val runPath: String) {
    private val mutableMap
        get() = map as MutableMap<String, SavedMatrix>

    fun update(id: String, savedMatrix: SavedMatrix) {
        mutableMap[id] = savedMatrix
    }
}

fun MatrixMap.isAllSuccessful() = map.values.any(SavedMatrix::isFailed).not()

fun Iterable<TestMatrix>.updateMatrixMap(matrixMap: MatrixMap) = forEach { matrix ->
    matrixMap.map[matrix.testMatrixId]?.updateWithMatrix(matrix)?.let {
        matrixMap.update(matrix.testMatrixId, it)
    }
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
 * @param shouldIgnore [Boolean]
 *        set {true} to ignore failed matrices and exit with status code 0
 *
 * @throws MatrixCanceledError [MatrixCanceledError]
 *         at least one matrix canceled by user
 * @throws InfrastructureError [InfrastructureError]
 *         at least one matrix have a test infrastructure error
 * @throws IncompatibleTestDimensionError [IncompatibleTestDimensionError]
 *         at least one matrix have a incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
 * @throws FTLError [FTLError]
 *         at least one matrix have unexpected error
 */

fun MatrixMap.validate(shouldIgnore: Boolean = false) {
    map.values.run {
        firstOrNull { it.canceledByUser() }?.run { throw MatrixCanceledError(outcomeDetails) }
        firstOrNull { it.infrastructureFail() }?.run { throw InfrastructureError(outcomeDetails) }
        firstOrNull { it.incompatibleFail() }?.run { throw IncompatibleTestDimensionError(outcomeDetails) }
        firstOrNull { it.invalid() }?.run { throw MatrixValidationError(errorMessage()) }
        firstOrNull { it.state != MatrixState.FINISHED }?.let { throw FTLError(it) }
        filter { it.isFailed() }.let {
            if (it.isNotEmpty()) throw FailedMatrixError(
                matrices = it,
                ignoreFailed = shouldIgnore
            )
        }
    }
}

private val SavedMatrix.outcomeDetails get() = testAxises.firstOrNull()?.details.orEmpty()

private fun SavedMatrix.errorMessage() = "Matrix: [$matrixId] failed: ".plus(getOutcomeMessageByKey(testAxises.firstOrNull()?.details.orUnknown()))
