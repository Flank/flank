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
import ftl.util.MatrixState.ERROR
import ftl.util.MatrixState.FINISHED
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
        val testExecutionsData = matrix.testExecutions.createTestExecutionDataListAsync()
        val summedTestSuiteOverviewData =
            testExecutionsData
                .prepareForJUnitResult()
                .fold(TestSuiteOverviewData(0, 0, 0, 0, 0, 0.0, 0.0)) { sum, test ->
                    sum + test.createTestSuitOverviewData()
                }

        testExecutionsData
            .forEach {
                val stepOutcome = GcToolResults.getExecutionResult(it.testExecution).outcome
                updatedFinishedInfo(
                    stepOutcome = stepOutcome,
                    flakyOutcome = stepOutcome.summary != it.step.outcome.summary,
                    testSuiteOverviewData = summedTestSuiteOverviewData,
                    isRoboTests = it.testExecution.testSpecification?.androidRoboTest != null,
                    isVirtualDevice = isVirtualDevice(
                        device = it.testExecution.environment.androidDevice,
                        projectId = matrix.projectId.orEmpty()
                    ),
                    billableMinutes = it.testExecution.getBillableMinutes()
                )
            }
    }

    private fun TestExecution.getBillableMinutes() =
        takeIf { testExecution -> testExecution.state != ERROR }
            ?.run {
                // testExecutionStep, testTiming, etc. can all be null.
                // sometimes testExecutionStep is present and testTiming is null
                val testTimeSeconds =
                    GcToolResults.getStepResult(toolResultsStep).testExecutionStep?.testTiming?.testProcessDuration?.seconds
                        ?: return@run null
                val testTimeout = timeoutToSeconds(testSpecification?.testTimeout ?: "0s")

                // if overall test duration time is higher then testTimeout flank should calculate billable minutes for testTimeout
                billableMinutes(min(testTimeSeconds, testTimeout))
            }

    private fun updatedFinishedInfo(
        stepOutcome: Outcome?,
        flakyOutcome: Boolean,
        testSuiteOverviewData: TestSuiteOverviewData?,
        isRoboTests: Boolean,
        isVirtualDevice: Boolean,
        billableMinutes: Long?
    ) {
        updateOutcome(stepOutcome, flakyOutcome)
        updateOutcomeDetails(stepOutcome, testSuiteOverviewData, isRoboTests)
        billableMinutes?.let { updateBillableMinutes(it, isVirtualDevice) }
    }

    private fun updateOutcome(
        stepOutcome: Outcome?,
        flakyOutcome: Boolean
    ) {
        outcome = when {
            flakyOutcome -> flaky
            // the matrix outcome is failure if any step fails
            // if the matrix outcome is already set to failure then we can ignore the other step outcomes.
            // inconclusive is treated as a failure
            outcome == failure || outcome == inconclusive -> return
            outcome == flaky -> stepOutcome?.summary?.takeIf { it == failure || it == inconclusive }
            else -> stepOutcome?.summary
        } ?: outcome
    }

    private fun updateOutcomeDetails(
        stepOutcome: Outcome?,
        testSuiteOverviewData: TestSuiteOverviewData?,
        isRoboTests: Boolean
    ) {
        outcomeDetails = if (isRoboTests) "Robo test" else (stepOutcome?.getDetails(testSuiteOverviewData) ?: "---")
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
