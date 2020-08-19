package ftl.json

import com.google.api.services.testing.model.TestMatrix
import ftl.reports.outcome.createMatrixOutcomeSummary
import ftl.reports.outcome.fetchTestOutcomeContext
import ftl.util.MatrixState.FINISHED
import ftl.util.MatrixState.INVALID
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.getClientDetails
import ftl.util.getGcsPath
import ftl.util.getGcsPathWithoutRootBucket
import ftl.util.getGcsRootBucket
import ftl.util.webLink
import ftl.util.webLinkWithoutExecutionDetails

// execution gcs paths aren't API accessible.
data class SavedMatrix(
    val matrixId: String,
    val state: String,
    val gcsPath: String,
    val webLink: String,
    val downloaded: Boolean,
    val billableVirtualMinutes: Long,
    val billablePhysicalMinutes: Long,
    val outcome: String,
    val outcomeDetails: String,
    val clientDetails: Map<String, String>?,
    val gcsPathWithoutRootBucket: String,
    val gcsRootBucket: String,
    val webLinkWithoutExecutionDetails: String
) {
    companion object {
        fun create(testMatrix: TestMatrix) = updatedSavedMatrix(testMatrix)
    }
}

fun SavedMatrix.canceledByUser() = outcomeDetails == ABORTED_BY_USER_MESSAGE

fun SavedMatrix.infrastructureFail() = outcomeDetails == INFRASTRUCTURE_FAILURE_MESSAGE

fun SavedMatrix.incompatibleFail() = outcomeDetails in arrayOf(
    INCOMPATIBLE_APP_VERSION_MESSAGE,
    INCOMPATIBLE_ARCHITECTURE_MESSAGE,
    INCOMPATIBLE_DEVICE_MESSAGE
)

fun SavedMatrix.isFailed() = when (outcome) {
    failure -> true
    skipped -> true
    inconclusive -> true
    else -> false
}

fun SavedMatrix.needsUpdate(newMatrix: TestMatrix): Boolean {
    val newState = newMatrix.state
    val newLink = newMatrix.webLink()
    val changedState = state != newState
    val changedLink = webLink != newLink
    return changedState || changedLink
}

internal fun SavedMatrix.updateMatrix(newMatrix: TestMatrix): SavedMatrix {
    return if (needsUpdate(newMatrix)) {
        return updatedSavedMatrix(newMatrix)
    } else this
}

private fun updatedSavedMatrix(newMatrix: TestMatrix): SavedMatrix {
    var outcomeDetails = ""
    var outcome = ""
    var billableVirtualMinutes = 0L
    var billablePhysicalMinutes = 0L
    when (newMatrix.state) {
        FINISHED -> {
            billableVirtualMinutes = 0
            billablePhysicalMinutes = 0
            outcome = success
            newMatrix.fetchTestOutcomeContext().createMatrixOutcomeSummary().let { (billableMinutes, summary) ->
                outcome = summary.outcome
                outcomeDetails = summary.testDetails
                billableVirtualMinutes = billableMinutes.virtual
                billablePhysicalMinutes = billableMinutes.physical
            }
        }
        INVALID -> {
            outcomeDetails = "Matrix is invalid"
            outcome = "---"
        }
    }
    return defaultSavedMatrix(newMatrix).copy(
        billableVirtualMinutes = billableVirtualMinutes,
        billablePhysicalMinutes = billablePhysicalMinutes,
        outcome = outcome,
        outcomeDetails = outcomeDetails
    )
}

private fun defaultSavedMatrix(testMatrix: TestMatrix) = SavedMatrix(
    matrixId = testMatrix.testMatrixId,
    state = testMatrix.state,
    gcsPath = testMatrix.getGcsPath(),
    webLink = testMatrix.webLink(),
    downloaded = false,
    billableVirtualMinutes = 0,
    billablePhysicalMinutes = 0,
    outcome = "",
    outcomeDetails = "",
    clientDetails = testMatrix.getClientDetails(),
    gcsPathWithoutRootBucket = testMatrix.getGcsPathWithoutRootBucket(),
    gcsRootBucket = testMatrix.getGcsRootBucket(),
    webLinkWithoutExecutionDetails = testMatrix.webLinkWithoutExecutionDetails()
)
