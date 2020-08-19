package ftl.json

import com.google.api.services.testing.model.TestMatrix
import ftl.reports.outcome.createMatrixOutcomeSummary
import ftl.reports.outcome.fetchTestOutcomeContext
import ftl.util.FlankGeneralError
import ftl.util.MatrixState.FINISHED
import ftl.util.MatrixState.INVALID
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
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

        updateFinishedMatrixData(matrix)
    }

    private fun updateFinishedMatrixData(matrix: TestMatrix) {
        matrix.fetchTestOutcomeContext().createMatrixOutcomeSummary().let { (billableMinutes, summary) ->
            outcome = summary.outcome
            outcomeDetails = summary.testDetails
            billableVirtualMinutes = billableMinutes.virtual
            billablePhysicalMinutes = billableMinutes.physical
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
