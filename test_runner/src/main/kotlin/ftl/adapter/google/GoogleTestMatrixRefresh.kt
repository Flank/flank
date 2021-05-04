package ftl.adapter.google

import com.google.testing.model.FileReference
import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import ftl.analytics.toJSONObject
import ftl.environment.orUnknown
import ftl.reports.outcome.BillableMinutes
import ftl.reports.outcome.TestOutcome
import ftl.reports.outcome.createMatrixOutcomeSummary
import ftl.reports.outcome.fetchTestOutcomeContext
import ftl.run.common.prettyPrint
import ftl.util.MatrixState
import ftl.util.getClientDetails
import ftl.util.getGcsPath
import ftl.util.getGcsPathWithoutRootBucket
import ftl.util.getGcsRootBucket
import ftl.util.timeoutToSeconds
import ftl.util.webLink
import ftl.util.webLinkWithoutExecutionDetails

fun TestMatrix.toApiModel() = ftl.api.TestMatrix.Data(
    projectId = projectId.orEmpty(),
    matrixId = testMatrixId.orEmpty(),
    state = state.orEmpty(),
    gcsPath = getGcsPath(),
    webLink = webLink(),
    downloaded = false,
    clientDetails = getClientDetails(),
    gcsPathWithoutRootBucket = getGcsPathWithoutRootBucket(),
    gcsRootBucket = getGcsRootBucket(),
    webLinkWithoutExecutionDetails = webLinkWithoutExecutionDetails(),
    appFileName = extractAppFileName() ?: fallbackAppName,
    isCompleted = MatrixState.completed(state) &&
        testExecutions?.all { MatrixState.completed(it.state.orEmpty()) } ?: true,
    testExecutions = testExecutions?.toApiModel().orEmpty(),
    testTimeout = testTimeout(),
    isRoboTest = isRoboTest(),
    historyId = resultStorage?.toolResultsExecution?.historyId.orEmpty(),
    executionId = resultStorage?.toolResultsExecution?.executionId.orEmpty(),
    invalidMatrixDetails = invalidMatrixDetails.orUnknown(),
)

private fun TestMatrix.testTimeout() = timeoutToSeconds(
    testExecutions
        ?.firstOrNull { it?.testSpecification?.testTimeout != null }
        ?.testSpecification
        ?.testTimeout
        ?: "0s"
)

private fun TestMatrix.isRoboTest() = testExecutions.orEmpty().any { it?.testSpecification?.androidRoboTest != null }

private const val fallbackAppName = "N/A"

internal fun ftl.api.TestMatrix.Data.updateWithMatrix(newMatrix: ftl.api.TestMatrix.Data): ftl.api.TestMatrix.Data =
    if (needsUpdate(newMatrix)) updatedSavedMatrix(newMatrix)
    else this

fun ftl.api.TestMatrix.Data.needsUpdate(newMatrix: ftl.api.TestMatrix.Data): Boolean {
    val newState = newMatrix.state
    val newLink = newMatrix.webLink
    val changedState = state != newState
    val changedLink = webLink != newLink
    return (changedState || changedLink)
}

private fun ftl.api.TestMatrix.Data.updatedSavedMatrix(
    newMatrix: ftl.api.TestMatrix.Data
): ftl.api.TestMatrix.Data = when (newMatrix.state) {
    state -> this

    MatrixState.FINISHED ->
        newMatrix.fetchTestOutcomeContext().createMatrixOutcomeSummary().let { (billableMinutes, outcomes) ->
            updateProperties(newMatrix).updateOutcome(
                outcomes.map {
                    it.toApiModel()
                }
            ).updateBillableMinutes(billableMinutes)
        }

    MatrixState.INVALID -> updateProperties(newMatrix).updateOutcome(
        listOf(
            newMatrix.invalidTestOutcome().toApiModel()
        )
    )

    else -> updateProperties(newMatrix)
}

private fun TestOutcome.toApiModel() = ftl.api.TestMatrix.Outcome(
    device, outcome, details,
    ftl.api.TestMatrix.SuiteOverview(
        testSuiteOverview.total,
        testSuiteOverview.errors,
        testSuiteOverview.failures,
        testSuiteOverview.flakes,
        testSuiteOverview.skipped,
        testSuiteOverview.elapsedTime,
        testSuiteOverview.overheadTime
    )
)

private fun ftl.api.TestMatrix.Data.updateProperties(newMatrix: ftl.api.TestMatrix.Data) = copy(
    matrixId = newMatrix.matrixId,
    state = newMatrix.state,
    gcsPath = newMatrix.gcsPath,
    webLink = newMatrix.webLink,
    downloaded = false,
    clientDetails = newMatrix.clientDetails,
    gcsPathWithoutRootBucket = newMatrix.gcsPathWithoutRootBucket,
    gcsRootBucket = newMatrix.gcsRootBucket,
    webLinkWithoutExecutionDetails = newMatrix.webLinkWithoutExecutionDetails,
    appFileName = newMatrix.appFileName,
    isCompleted = MatrixState.completed(state) &&
        newMatrix.testExecutions.all { MatrixState.completed(it.state) },
    testExecutions = newMatrix.testExecutions
)

fun List<TestExecution>.toApiModel() = map(TestExecution::toApiModel)

fun TestExecution.toApiModel() = ftl.api.TestMatrix.TestExecution(
    id = id.orEmpty(),
    modelId = environment?.androidDevice?.androidModelId
        ?: environment?.iosDevice?.iosModelId ?: "",
    deviceVersion = environment?.androidDevice?.androidVersionId
        ?: environment?.iosDevice?.iosVersionId ?: "",
    shardIndex = shard?.shardIndex ?: 0,
    state = state.orUnknown(),
    errorMessage = testDetails?.errorMessage.orEmpty(),
    progress = testDetails?.progressMessages ?: emptyList()
)

private fun TestMatrix.extractAppFileName() = testSpecification?.run {
    listOf(
        androidInstrumentationTest,
        androidTestLoop,
        androidRoboTest,
        iosXcTest,
        iosTestLoop
    )
        .firstOrNull { it != null }
        ?.toJSONObject()
        ?.let { prettyPrint.fromJson(it.toString(), AppPath::class.java).gcsPath }
        ?.substringAfterLast('/')
}

private fun ftl.api.TestMatrix.Data.updateBillableMinutes(billableMinutes: BillableMinutes) = copy(
    billableMinutes = ftl.api.TestMatrix.BillableMinutes(billableMinutes.virtual, billableMinutes.physical)
)

private fun ftl.api.TestMatrix.Data.updateOutcome(outcome: List<ftl.api.TestMatrix.Outcome>) = copy(
    axes = outcome
)

private fun ftl.api.TestMatrix.Data.invalidTestOutcome() = TestOutcome(
    outcome = MatrixState.INVALID,
    details = invalidMatrixDetails.orUnknown()
)

private data class AppPath(
    private val appApk: FileReference?,
    private val testsZip: FileReference?,
    private val appIpa: FileReference?
) {
    val gcsPath: String?
        get() = (appApk ?: testsZip ?: appIpa)?.gcsPath
}
/*

private fun TestMatrix.badMatrixError() = BadMatrixError(this)

class BadMatrixError(matrix: TestMatrix) : FTLError(matrix)
*/
