package ftl.json

import ftl.api.TestMatrix
import ftl.domain.testmatrix.updateWithMatrix
import ftl.environment.orUnknown
import ftl.reports.outcome.getOutcomeMessageByKey
import ftl.run.exception.FTLError
import ftl.run.exception.FailedMatrixError
import ftl.run.exception.IncompatibleTestDimensionError
import ftl.run.exception.InfrastructureError
import ftl.run.exception.MatrixCanceledError
import ftl.run.exception.MatrixValidationError
import ftl.util.MatrixState
import ftl.util.StepOutcome

data class MatrixMap(val map: Map<String, TestMatrix.Data>, val runPath: String) {
    private val mutableMap
        get() = map as MutableMap<String, TestMatrix.Data>

    fun update(id: String, savedMatrix: TestMatrix.Data) {
        mutableMap[id] = savedMatrix
    }
}

fun MatrixMap.isAllSuccessful() = map.values.any(TestMatrix.Data::isFailed).not()

fun Iterable<TestMatrix.Data>.updateMatrixMap(matrixMap: MatrixMap) {
    forEach { matrix ->
        matrixMap.map[matrix.matrixId]?.updateWithMatrix(matrix)?.let {
            matrixMap.update(matrix.matrixId, it)
        }
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
        firstOrNull { it.canceledByUser }?.run { throw MatrixCanceledError(outcomeDetails) }
        firstOrNull { it.infrastructureFail }?.run { throw InfrastructureError(outcomeDetails) }
        firstOrNull { it.incompatibleFail }?.run { throw IncompatibleTestDimensionError(outcomeDetails) }
        firstOrNull { it.invalid }?.run { throw MatrixValidationError(errorMessage()) }
        firstOrNull { it.state != MatrixState.FINISHED }?.let { throw FTLError(it) }
        filter { it.isFailed() }.let {
            if (it.isNotEmpty()) throw FailedMatrixError(
                matrices = it,
                ignoreFailed = shouldIgnore
            )
        }
    }
}

fun TestMatrix.Data.isFailed() = when (outcome) {
    StepOutcome.failure -> true
    StepOutcome.skipped -> true
    StepOutcome.inconclusive -> true
    MatrixState.INVALID -> true
    else -> false
}

private val TestMatrix.Data.canceledByUser: Boolean
    get() = axes.any { it.details == ABORTED_BY_USER_MESSAGE }

private val TestMatrix.Data.infrastructureFail: Boolean
    get() = axes.any { it.details == INFRASTRUCTURE_FAILURE_MESSAGE }

private val TestMatrix.Data.incompatibleFail: Boolean
    get() = axes.map { it.details }.intersect(incompatibleFails).isNotEmpty()

private val TestMatrix.Data.invalid: Boolean
    get() = axes.any { it.outcome == MatrixState.INVALID }

private val incompatibleFails = setOf(
    INCOMPATIBLE_APP_VERSION_MESSAGE,
    INCOMPATIBLE_ARCHITECTURE_MESSAGE,
    INCOMPATIBLE_DEVICE_MESSAGE
)

private val TestMatrix.Data.outcomeDetails get() = axes.firstOrNull()?.details.orEmpty()

private fun TestMatrix.Data.errorMessage() = "Matrix: [$matrixId] failed: ".plus(getOutcomeMessageByKey(axes.firstOrNull()?.details.orUnknown()))
