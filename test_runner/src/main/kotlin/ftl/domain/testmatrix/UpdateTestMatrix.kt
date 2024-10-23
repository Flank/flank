package ftl.domain.testmatrix

import ftl.api.TestMatrix
import ftl.api.fetchTestSummary
import ftl.environment.orUnknown
import ftl.util.MatrixState

internal fun TestMatrix.Data.updateWithMatrix(newMatrix: TestMatrix.Data): TestMatrix.Data =
    if (needsUpdate(newMatrix)) updatedSavedMatrix(newMatrix)
    else this

fun TestMatrix.Data.needsUpdate(newMatrix: TestMatrix.Data): Boolean {
    val newState = newMatrix.state
    val newLink = newMatrix.webLink
    val changedState = state != newState
    val changedLink = webLink != newLink
    return (changedState || changedLink)
}

private fun TestMatrix.Data.updatedSavedMatrix(
    newMatrix: TestMatrix.Data
): TestMatrix.Data = when (newMatrix.state) {
    state -> this
    MatrixState.FINISHED -> {
        val (billableMinutes, outcomes) = fetchTestSummary(newMatrix)
        updateProperties(newMatrix)
            .updateOutcome(outcomes)
            .updateBillableMinutes(billableMinutes)
    }

    MatrixState.INVALID -> updateProperties(newMatrix).updateOutcome(listOf(newMatrix.invalidTestOutcome()))
    else -> updateProperties(newMatrix)
}

private fun TestMatrix.Data.updateProperties(newMatrix: TestMatrix.Data) = copy(
    matrixId = newMatrix.matrixId,
    state = newMatrix.state,
    gcsPath = newMatrix.gcsPath,
    webLink = newMatrix.webLink,
    downloaded = false,
    clientDetails = newMatrix.clientDetails,
    gcsPathWithoutRootBucket = newMatrix.gcsPathWithoutRootBucket,
    gcsRootBucket = newMatrix.gcsRootBucket,
    appFileName = newMatrix.appFileName,
    testFileName = newMatrix.testFileName,
    isCompleted = MatrixState.completed(state) &&
        newMatrix.testExecutions.all { MatrixState.completed(it.state) },
    testExecutions = newMatrix.testExecutions,
    isRoboTest = newMatrix.isRoboTest,
)

private fun TestMatrix.Data.updateBillableMinutes(billableMinutes: TestMatrix.BillableMinutes) = copy(
    billableMinutes = TestMatrix.BillableMinutes(billableMinutes.virtual, billableMinutes.physical)
)

private fun TestMatrix.Data.updateOutcome(outcome: List<TestMatrix.Outcome>) = copy(
    axes = outcome
)

private fun TestMatrix.Data.invalidTestOutcome() = TestMatrix.Outcome(
    outcome = MatrixState.INVALID,
    details = invalidMatrixDetails.orUnknown()
)
