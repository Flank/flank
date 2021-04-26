package ftl.json

import com.google.testing.model.FileReference
import com.google.testing.model.TestMatrix
import ftl.analytics.toJSONObject
import ftl.environment.orUnknown
import ftl.reports.outcome.BillableMinutes
import ftl.reports.outcome.TestOutcome
import ftl.reports.outcome.createMatrixOutcomeSummary
import ftl.reports.outcome.fetchTestOutcomeContext
import ftl.run.common.prettyPrint
import ftl.util.MatrixState.FINISHED
import ftl.util.MatrixState.INVALID
import ftl.util.StepOutcome
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
    // this is variable intentionally, this is workaround to pass changes while fetching artifacts
    // todo think about different solution
    var downloaded: Boolean = false,
    val billableVirtualMinutes: Long = 0,
    val billablePhysicalMinutes: Long = 0,
    val clientDetails: Map<String, String>? = null,
    val gcsPathWithoutRootBucket: String = "",
    val gcsRootBucket: String = "",
    val webLinkWithoutExecutionDetails: String? = "",
    val testAxises: List<TestOutcome> = emptyList(),
    val appFileName: String = fallbackAppName
) {
    val outcome = testAxises.maxByOrNull { StepOutcome.order.indexOf(it.outcome) }?.outcome.orEmpty()
}

private const val fallbackAppName = "N/A"

fun createSavedMatrix(testMatrix: TestMatrix) = SavedMatrix().updateWithMatrix(testMatrix)

fun SavedMatrix.canceledByUser() = testAxises.any { it.details == ABORTED_BY_USER_MESSAGE }

fun SavedMatrix.infrastructureFail() = testAxises.any { it.details == INFRASTRUCTURE_FAILURE_MESSAGE }

fun SavedMatrix.incompatibleFail() = testAxises.map { it.details }.intersect(incompatibleFails).isNotEmpty()

fun SavedMatrix.invalid() = testAxises.any { it.outcome == INVALID }

private val incompatibleFails = setOf(
    INCOMPATIBLE_APP_VERSION_MESSAGE,
    INCOMPATIBLE_ARCHITECTURE_MESSAGE,
    INCOMPATIBLE_DEVICE_MESSAGE
)

fun SavedMatrix.isFailed() = when (outcome) {
    failure -> true
    skipped -> true
    inconclusive -> true
    INVALID -> true
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

    FINISHED ->
        newMatrix.fetchTestOutcomeContext().createMatrixOutcomeSummary().let { (billableMinutes, outcomes) ->
            updateProperties(newMatrix).updateOutcome(outcomes).updateBillableMinutes(billableMinutes)
        }

    INVALID -> updateProperties(newMatrix).updateOutcome(listOf(newMatrix.invalidTestOutcome()))

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
    webLinkWithoutExecutionDetails = newMatrix.webLinkWithoutExecutionDetails(),
    appFileName = newMatrix.extractAppFileName() ?: fallbackAppName
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

private fun SavedMatrix.updateBillableMinutes(billableMinutes: BillableMinutes) = copy(
    billablePhysicalMinutes = billableMinutes.physical,
    billableVirtualMinutes = billableMinutes.virtual,
)

private fun SavedMatrix.updateOutcome(outcome: List<TestOutcome>) = copy(
    testAxises = outcome
)

private fun TestMatrix.invalidTestOutcome() = TestOutcome(
    outcome = INVALID,
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
