package ftl.json

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.toolresults.model.Outcome
import ftl.android.AndroidCatalog.isVirtualDevice
import ftl.gc.GcToolResults
import ftl.reports.api.createTestExecutionDataListAsync
import ftl.reports.api.createTestSuitOverviewData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.api.prepareForJUnitResult
import ftl.util.FlankGeneralError
import ftl.util.MatrixState.ERROR
import ftl.util.MatrixState.FINISHED
import ftl.util.MatrixState.INVALID
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.billableMinutes
import ftl.util.timeoutToSeconds
import ftl.util.webLink
import kotlin.math.min

// execution gcs paths aren't API accessible.
class SavedMatrix(matrix: TestMatrix) {
    val matrixId: String = matrix.testMatrixId
    var state: String = matrix.state
        private set
    val gcsPath: String = matrix.resultStorage.googleCloudStorage.gcsPath
    var webLink: String = matrix.webLink()
        private set
    var downloaded = false

    var billableVirtualMinutes: Long = 0
        private set
    var billablePhysicalMinutes: Long = 0
        private set
    var outcome: String = ""
        private set
    var outcomeDetails: String = ""
        private set
    val clientDetails = matrix.clientInfo?.clientInfoDetails
        ?.map { it.key to it.value }
        ?.toMap()

    init {
        if (this.state == FINISHED) finished(matrix)
    }

    // ExitCodeFromRollupOutcome - https://github.com/bootstraponline/gcloud_cli/blob/137d864acd5928baf25434cf59b0225c4d1f9319/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/exit_code.py#L46
    fun failed(): Boolean {
        // outcome of the test execution.
        // skipped means unsupported environment
        return when (outcome) {
            failure -> true
            skipped -> true
            inconclusive -> true
            else -> false
        }
    }

    /** return true if the content changed **/
    fun update(matrix: TestMatrix): Boolean {
        val newState = matrix.state
        val newLink = matrix.webLink()
        val changedState = state != newState
        val changedLink = webLink != newLink

        if (changedState) {
            updateState(newState, matrix)
        }

        if (changedLink) {
            this.webLink = newLink
        }

        return changedState || changedLink
    }

    private fun updateState(newState: String, testMatrix: TestMatrix) {
        state = newState
        when (state) {
            FINISHED -> finished(testMatrix)
            INVALID -> {
                outcomeDetails = "Matrix is invalid"
                outcome = "---"
            }
        }
    }

    private fun finished(matrix: TestMatrix) {
        if (matrix.state != FINISHED) {
            throw FlankGeneralError("Matrix ${matrix.testMatrixId} ${matrix.state} != $FINISHED")
        }
        billableVirtualMinutes = 0
        billablePhysicalMinutes = 0
        outcome = success

        updateFinishedMatrixData2(matrix)
    }

    private fun updateFinishedMatrixData2(matrix: TestMatrix) {
        matrix.createMatrixOutcomeSummary()?.let { summary ->
            outcome = summary.outcome
            outcomeDetails = summary.testDetails
        }
    }

    @Suppress("unused")
    private fun updateFinishedMatrixData(matrix: TestMatrix) {
        val testExecutionsData = matrix.testExecutions?.createTestExecutionDataListAsync() ?: return
        val summedTestSuiteOverviewData =
            testExecutionsData
                .prepareForJUnitResult()
                .fold(TestSuiteOverviewData(0, 0, 0, 0, 0, 0.0, 0.0)) { sum, test ->
                    sum + test.createTestSuitOverviewData()
                }

        testExecutionsData
            .forEach {
                with(GcToolResults.getExecutionResult(it.testExecution).outcome) {
                    updateOutcome(it.step.outcome?.summary != this?.summary)
                    updateOutcomeDetails(
                        testSuiteOverviewData = summedTestSuiteOverviewData,
                        isRoboTests = it.testExecution.testSpecification?.androidRoboTest != null
                    )
                }
                it.testExecution.getBillableMinutes()?.let { billableMinutes ->
                    updateBillableMinutes(
                        billableMinutes = billableMinutes,
                        isVirtualDevice = isVirtualDevice(
                            it.testExecution.environment.androidDevice,
                            matrix.projectId.orEmpty()
                        )
                    )
                }
            }
    }

    private fun TestExecution.getBillableMinutes() =
        takeIf { testExecution -> testExecution.state != ERROR }
            ?.run {
                // testExecutionStep, testTiming, etc. can all be null.
                // sometimes testExecutionStep is present and testTiming is null
                val testTimeSeconds =
                    GcToolResults.getStepResult(toolResultsStep).testExecutionStep?.testTiming?.testProcessDuration?.seconds
                        ?: return null
                val testTimeout = timeoutToSeconds(testSpecification?.testTimeout ?: "0s")

                // if overall test duration time is higher then testTimeout flank should calculate billable minutes for testTimeout
                billableMinutes(min(testTimeSeconds, testTimeout))
            }

    private fun Outcome?.updateOutcome(
        flakyOutcome: Boolean
    ) {
        outcome = when {
            // The matrix outcome is failure if any step fails
            // If the matrix outcome is already set to failure then we can ignore the other step outcomes.
            // Inconclusive is treated as a failure
            // This particular conditions order should not be changed, for more details check:
            // https://github.com/Flank/flank/issues/914
            outcome == failure || outcome == inconclusive -> return
            flakyOutcome -> flaky
            outcome == flaky -> this?.summary?.takeIf { it == failure || it == inconclusive }
            else -> this?.summary
        } ?: outcome
    }

    private fun Outcome?.updateOutcomeDetails(
        testSuiteOverviewData: TestSuiteOverviewData?,
        isRoboTests: Boolean
    ) {
        outcomeDetails = if (isRoboTests) "Robo test" else (this?.getDetails(testSuiteOverviewData) ?: "---")
    }

    private fun updateBillableMinutes(billableMinutes: Long, isVirtualDevice: Boolean) {
        if (isVirtualDevice) {
            billableVirtualMinutes += billableMinutes
        } else {
            billablePhysicalMinutes += billableMinutes
        }
    }

    val gcsPathWithoutRootBucket get() = gcsPath.substringAfter('/')
    val gcsRootBucket get() = gcsPath.substringBefore('/')
    val webLinkWithoutExecutionDetails: String
        get() {
            return if (webLink.isEmpty()) {
                webLink
            } else {
                val executionsRegex = "/executions/.+".toRegex()
                val foundValue = executionsRegex.find(webLink)?.value.orEmpty()
                webLink.removeSuffix(foundValue)
            }
        }
}

fun SavedMatrix.canceledByUser() = outcomeDetails == ABORTED_BY_USER_MESSAGE

fun SavedMatrix.infrastructureFail() = outcomeDetails == INFRASTRUCTURE_FAILURE_MESSAGE

fun SavedMatrix.incompatibleFail() = outcomeDetails in arrayOf(INCOMPATIBLE_APP_VERSION_MESSAGE, INCOMPATIBLE_ARCHITECTURE_MESSAGE, INCOMPATIBLE_DEVICE_MESSAGE)
