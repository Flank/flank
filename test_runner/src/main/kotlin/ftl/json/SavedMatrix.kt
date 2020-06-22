package ftl.json

import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.toolresults.model.Outcome
import ftl.android.AndroidCatalog
import ftl.gc.GcToolResults
import ftl.reports.api.createTestExecutionDataListAsync
import ftl.reports.api.createTestSuitOverviewData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.Billing
import ftl.util.MatrixState.FINISHED
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.getDetails
import ftl.util.webLink

private data class FinishedTestMatrixData(
    val stepOutcome: Outcome,
    val isVirtualDevice: Boolean,
    val testSuiteOverviewData: TestSuiteOverviewData?,
    val billableMinutes: Long?
)

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
            this.state = newState
            if (this.state == FINISHED) finished(matrix)
        }

        if (changedLink) {
            this.webLink = newLink
        }

        return changedState || changedLink
    }

    private fun finished(matrix: TestMatrix) {
        if (matrix.state != FINISHED) {
            throw RuntimeException("Matrix ${matrix.testMatrixId} ${matrix.state} != $FINISHED")
        }
        billableVirtualMinutes = 0
        billablePhysicalMinutes = 0
        outcome = success
        if (matrix.testExecutions == null) return

        updateFinishedMatrixData(matrix)
    }

    private fun updateFinishedMatrixData(matrix: TestMatrix) {
        matrix.testExecutions.createTestExecutionDataListAsync()
            .map {
                FinishedTestMatrixData(
                    stepOutcome = GcToolResults.getExecutionResult(it.testExecution).outcome,
                    isVirtualDevice = AndroidCatalog.isVirtualDevice(it.testExecution.environment.androidDevice, matrix.projectId.orEmpty()),
                    testSuiteOverviewData = it.createTestSuitOverviewData(),
                    billableMinutes = it.step.testExecutionStep?.testTiming?.testProcessDuration?.seconds
                        ?.let { testTimeSeconds -> Billing.billableMinutes(testTimeSeconds) }
                )
            }
            .forEach { (stepOutcome, isVirtualDevice, testSuiteOverviewData, billableMinutes) ->
                updateOutcome(stepOutcome)
                updateOutcomeDetails(stepOutcome, testSuiteOverviewData)
                billableMinutes?.let { updateBillableMinutes(it, isVirtualDevice) }
            }
    }

    private fun updateOutcome(stepOutcome: Outcome) {
        // the matrix outcome is failure if any step fails
        // if the matrix outcome is already set to failure then we can ignore the other step outcomes.
        // inconclusive is treated as a failure
        if (outcome == failure || outcome == inconclusive) return

        outcome = stepOutcome.summary

        // Treat flaky outcome as a success
        if (outcome == flaky) outcome = success
    }

    private fun updateOutcomeDetails(stepOutcome: Outcome, testSuiteOverviewData: TestSuiteOverviewData?) {
        outcomeDetails = stepOutcome.getDetails(testSuiteOverviewData) ?: "---"
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
