package ftl.client.google

import com.google.common.annotations.VisibleForTesting
import com.google.testing.Testing
import com.google.testing.model.Account
import com.google.testing.model.ClientInfo
import com.google.testing.model.EnvironmentMatrix
import com.google.testing.model.GoogleAuto
import com.google.testing.model.GoogleCloudStorage
import com.google.testing.model.ResultStorage
import com.google.testing.model.TestMatrix
import com.google.testing.model.TestSetup
import com.google.testing.model.TestSpecification
import com.google.testing.model.ToolResultsHistory
import flank.common.join
import ftl.api.TestMatrixAndroid
import ftl.gc.GcAndroidDevice
import ftl.gc.android.mapGcsPathsToApks
import ftl.gc.android.mapToDeviceFiles
import ftl.gc.android.mapToDeviceObbFiles
import ftl.gc.android.setEnvironmentVariables
import ftl.gc.android.setupAndroidTest
import ftl.http.executeWithRetry
import ftl.run.exception.FlankGeneralError
import ftl.util.timeoutToSeconds
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun executeAndroidTests(
    config: TestMatrixAndroid.Config,
    testMatrixTypes: List<TestMatrixAndroid.Type>,
): List<TestMatrix> = testMatrixTypes
    .foldIndexed(emptyList<Deferred<TestMatrix>>()) { testMatrixTypeIndex, testMatrices, testMatrixType ->
        testMatrices + executeAndroidTestMatrix(testMatrixType, testMatrixTypeIndex, config)
    }.awaitAll()

private suspend fun executeAndroidTestMatrix(
    type: TestMatrixAndroid.Type,
    typeIndex: Int,
    config: TestMatrixAndroid.Config
): List<Deferred<TestMatrix>> = coroutineScope {
    (0 until config.repeatTests).map { runIndex ->
        async(Dispatchers.IO) {
            createAndroidTestMatrix(type, config, typeIndex, runIndex).executeWithRetry()
        }
    }
}

private fun createAndroidTestMatrix(
    testMatrixType: TestMatrixAndroid.Type,
    config: TestMatrixAndroid.Config,
    contextIndex: Int,
    runIndex: Int
): Testing.Projects.TestMatrices.Create {

    val testMatrix = TestMatrix()
        .setClientInfo(config.clientInfo)
        .setTestSpecification(getTestSpecification(testMatrixType, config))
        .setResultStorage(config.resultsStorage(contextIndex, runIndex))
        .setEnvironmentMatrix(config.environmentMatrix)
        .setFlakyTestAttempts(config.flakyTestAttempts)
        .setFailFast(config.failFast)

    return runCatching {
        GcTesting.get.projects().testMatrices().create(config.project, testMatrix)
    }.getOrElse { e -> throw FlankGeneralError(e) }
}

// https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
private val TestMatrixAndroid.Config.clientInfo
    get() = ClientInfo()
        .setName("Flank")
        .setClientInfoDetails(clientDetails?.toClientInfoDetailList())

private val TestMatrixAndroid.Config.environmentMatrix
    get() = EnvironmentMatrix()
        .setAndroidDeviceList(GcAndroidDevice.build(devices))

private fun getTestSpecification(
    testMatrixType: TestMatrixAndroid.Type,
    config: TestMatrixAndroid.Config
): TestSpecification = TestSpecification()
    .setDisablePerformanceMetrics(!config.performanceMetrics)
    .setDisableVideoRecording(!config.recordVideo)
    .setTestTimeout("${timeoutToSeconds(config.testTimeout)}s")
    .setTestSetup(getTestSetup(testMatrixType, config))
    .setupAndroidTest(testMatrixType)

private fun getTestSetup(
    testMatrixType: TestMatrixAndroid.Type,
    config: TestMatrixAndroid.Config
): TestSetup = TestSetup()
    .setAccount(config.account)
    .setNetworkProfile(config.networkProfile)
    .setDirectoriesToPull(config.directoriesToPull)
    .setAdditionalApks(config.additionalApkGcsPaths.mapGcsPathsToApks())
    .setFilesToPush(config.otherFiles.mapToDeviceFiles() + config.obbFiles.mapToDeviceObbFiles(config.obbNames))
    .setDontAutograntPermissions(config.autoGrantPermissions.not())
    .setEnvironmentVariables(config.environmentVariables, testMatrixType)

private fun TestMatrixAndroid.Config.resultsStorage(
    contextIndex: Int,
    runIndex: Int
) = ResultStorage()
    .setGoogleCloudStorage(
        GoogleCloudStorage().setGcsPath(join(resultsBucket, resultsDir.createGcsPath(contextIndex, runIndex)))
    )
    .setToolResultsHistory(
        ToolResultsHistory().setHistoryId(resultsHistoryName).setProjectId(project)
    )

@VisibleForTesting
internal fun String.createGcsPath(contextIndex: Int, runIndex: Int) =
    if (runIndex == 0) "$this/matrix_$contextIndex/"
    else "$this/matrix_${contextIndex}_$runIndex/"

private val TestMatrixAndroid.Config.account
    get() = if (autoGoogleLogin) Account().setGoogleAuto(GoogleAuto()) else null
