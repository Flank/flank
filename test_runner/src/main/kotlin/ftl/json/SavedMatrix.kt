package ftl.json

import com.google.api.services.testing.model.TestMatrix
import ftl.reports.outcome.BillableMinutes
import ftl.reports.outcome.TestOutcome
import ftl.reports.outcome.createMatrixOutcomeSummary
import ftl.reports.outcome.fetchTestOutcomeContext
import ftl.util.MatrixState.FINISHED
import ftl.util.MatrixState.INVALID
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.getClientDetails
import ftl.util.getGcsPath
import ftl.util.getGcsPathWithoutRootBucket
import ftl.util.getGcsRootBucket
import ftl.util.webLink
import ftl.util.webLinkWithoutExecutionDetails

// execution gcs paths aren't API accessible.
data class SavedMatrix(
    val matrixId: String = "",
    val state: String = "",
    val gcsPath: String = "",
    val webLink: String = "",
    val downloaded: Boolean = false,
    val billableVirtualMinutes: Long = 0,
    val billablePhysicalMinutes: Long = 0,
    val outcome: String = "",
    val outcomeDetails: String = "",
    val clientDetails: Map<String, String>? = null,
    val gcsPathWithoutRootBucket: String = "",
    val gcsRootBucket: String = "",
    val webLinkWithoutExecutionDetails: String? = "",
)

fun createSavedMatrix(testMatrix: TestMatrix) = SavedMatrix().updateWithMatrix(testMatrix)

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
    return (changedState || changedLink)
}

internal fun SavedMatrix.updateWithMatrix(newMatrix: TestMatrix): SavedMatrix =
    if (needsUpdate(newMatrix)) updatedSavedMatrix(newMatrix)
    else this

private fun SavedMatrix.updatedSavedMatrix(
    newMatrix: TestMatrix
): SavedMatrix = when (newMatrix.state) {
    state -> this

    FINISHED -> newMatrix.fetchTestOutcomeContext().createMatrixOutcomeSummary().let { (billableMinutes, outcome) ->
        updateProperties(newMatrix).updateOutcome(outcome).updateBillableMinutes(billableMinutes)
    }

    INVALID -> updateProperties(newMatrix).updateOutcome(invalidTestOutcome())

    else -> updateProperties(newMatrix)
}

private fun SavedMatrix.updateProperties(newMatrix: TestMatrix) = copy(
    matrixId = newMatrix.testMatrixId,
    state = newMatrix.state,
    gcsPath = newMatrix.getGcsPath(),
    webLink = newMatrix.webLink(),
    downloaded = false,
    clientDetails = newMatrix.getClientDetails(),
    gcsPathWithoutRootBucket = newMatrix.getGcsPathWithoutRootBucket(),
    gcsRootBucket = newMatrix.getGcsRootBucket(),
    webLinkWithoutExecutionDetails = newMatrix.webLinkWithoutExecutionDetails()
)

private fun SavedMatrix.updateBillableMinutes(billableMinutes: BillableMinutes) = copy(
    billablePhysicalMinutes = billableMinutes.physical,
    billableVirtualMinutes = billableMinutes.virtual,
)

private fun SavedMatrix.updateOutcome(testOutcome: TestOutcome) = copy(
    outcome = testOutcome.outcome,
    outcomeDetails = testOutcome.testDetails
)

private fun invalidTestOutcome() = TestOutcome(
    outcome = "---",
    testDetails = "Matrix is invalid"
)
