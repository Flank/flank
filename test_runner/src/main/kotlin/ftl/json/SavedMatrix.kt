package ftl.json

import com.google.api.client.json.GenericJson
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.toolresults.model.Outcome
import ftl.android.AndroidCatalog
import ftl.gc.GcToolResults
import ftl.util.MatrixState.FINISHED
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.StepOutcome.unset
import ftl.util.webLink

// execution gcs paths aren't API accessible.
class SavedMatrix(matrix: TestMatrix) {
    val matrixId: String = matrix.testMatrixId
    var state: String = matrix.state
        private set
    val gcsPath: String = matrix.resultStorage.googleCloudStorage.gcsPath
    var webLink: String = matrix.webLink()
        private set
    var downloaded = false

    var billableVirtualSeconds: Long = 0
        private set
    var billablePhysicalSeconds: Long = 0
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
        billableVirtualSeconds = 0
        billablePhysicalSeconds = 0
        outcome = success
        if (matrix.testExecutions == null) return

        matrix.testExecutions.forEach {
            val executionResult = GcToolResults.getExecutionResult(it)
            updateOutcome(executionResult.outcome)

            // testExecutionStep, testTiming, etc. can all be null.
            // sometimes testExecutionStep is present and testTiming is null
            val stepResult = GcToolResults.getStepResult(it.toolResultsStep)
            val testTimeSeconds = stepResult.testExecutionStep?.testTiming?.testProcessDuration?.seconds ?: return

            if (AndroidCatalog.isVirtualDevice(it.environment?.androidDevice, matrix.projectId ?: "")) {
                billableVirtualSeconds += testTimeSeconds
            } else {
                billablePhysicalSeconds += testTimeSeconds
            }
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

        outcomeDetails = when (outcome) {
            failure -> stepOutcome.failureDetail.keysToString()
            success -> stepOutcome.successDetail.keysToString()
            inconclusive -> stepOutcome.inconclusiveDetail.keysToString()
            skipped -> stepOutcome.skippedDetail.keysToString()
            unset -> "unset"
            else -> "unknown"
        }
    }

    private fun GenericJson?.keysToString(): String {
        return this?.keys?.joinToString(",") ?: ""
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
