package ftl.adapter.google

import com.google.testing.model.FileReference
import com.google.testing.model.TestExecution
import com.google.testing.model.TestMatrix
import ftl.analytics.toJSONObject
import ftl.api.TestMatrix.Data
import ftl.api.TestMatrix.Outcome
import ftl.api.TestMatrix.SuiteOverview
import ftl.client.google.TestOutcome
import ftl.environment.orUnknown
import ftl.run.common.prettyPrint
import ftl.util.MatrixState
import ftl.util.getClientDetails
import ftl.util.getGcsPath
import ftl.util.getGcsPathWithoutRootBucket
import ftl.util.getGcsRootBucket
import ftl.util.timeoutToSeconds
import ftl.util.webLink
import ftl.util.webLinkWithoutExecutionDetails

fun TestMatrix.toApiModel(identity: ftl.api.TestMatrix.Identity? = null) = Data(
    projectId = projectId.orEmpty(),
    matrixId = testMatrixId.orEmpty(),
    gcsPath = getGcsPath(),
    webLink = webLink(),
    downloaded = false,
    clientDetails = getClientDetails(),
    gcsPathWithoutRootBucket = getGcsPathWithoutRootBucket(),
    gcsRootBucket = getGcsRootBucket(),
    webLinkWithoutExecutionDetails = webLinkWithoutExecutionDetails(),
    appFileName = extractAppFileName() ?: fallbackAppName,
    testFileName = extractTestFileName() ?: fallbackAppName,
    isCompleted = MatrixState.completed(state) &&
        testExecutions?.all { MatrixState.completed(it.state.orEmpty()) } ?: true,
    testExecutions = testExecutions?.toApiModel().orEmpty(),
    testTimeout = testTimeout(),
    isRoboTest = isRoboTest(),
    historyId = resultStorage?.toolResultsExecution?.historyId ?: identity?.historyId.orEmpty(),
    executionId = resultStorage?.toolResultsExecution?.executionId ?: identity?.executionId.orEmpty(),
    invalidMatrixDetails = invalidMatrixDetails.orUnknown(),
    state = state.orEmpty(),
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

fun TestOutcome.toApiModel() = Outcome(
    device, outcome, details,
    SuiteOverview(
        testSuiteOverview.total,
        testSuiteOverview.errors,
        testSuiteOverview.failures,
        testSuiteOverview.flakes,
        testSuiteOverview.skipped,
        testSuiteOverview.elapsedTime,
        testSuiteOverview.overheadTime
    )
)

fun List<TestExecution>.toApiModel() = map(TestExecution::toApiModel)

fun TestExecution.toApiModel() = ftl.api.TestMatrix.TestExecution(
    id = id.orEmpty(),
    modelId = environment?.androidDevice?.androidModelId
        ?: environment?.iosDevice?.iosModelId ?: "",
    deviceVersion = environment?.androidDevice?.androidVersionId
        ?: environment?.iosDevice?.iosVersionId ?: "",
    shardIndex = shard?.shardIndex,
    state = state.orUnknown(),
    errorMessage = testDetails?.errorMessage.orEmpty(),
    progress = testDetails?.progressMessages ?: emptyList(),
    toolResultsStep = toolResultsStep
)

private fun TestMatrix.extractAppFileName() = extractFileName { gcsPath }

private fun TestMatrix.extractTestFileName() = extractFileName { testFile }

private fun TestMatrix.extractFileName(fileName: TestFilesPathWrapper.() -> String?) = testSpecification?.run {
    listOf(
        androidInstrumentationTest,
        androidTestLoop,
        androidRoboTest,
        iosXcTest,
        iosTestLoop
    )
        .firstOrNull { it != null }
        ?.toJSONObject()
        ?.let { prettyPrint.fromJson(it.toString(), TestFilesPathWrapper::class.java).fileName() }
        ?.substringAfterLast('/')
}

private data class TestFilesPathWrapper(
    private val appApk: FileReference?,
    private val testsZip: FileReference?,
    private val appIpa: FileReference?,
    private val xctestrun: FileReference?,
    private val testApk: FileReference?,
    private val roboScript: FileReference?
) {
    val gcsPath: String?
        get() = (appApk ?: testsZip ?: appIpa)?.gcsPath

    val testFile: String?
        get() = (testApk ?: xctestrun ?: roboScript)?.gcsPath
}
