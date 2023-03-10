package ftl.client.google.run.ios

import com.google.testing.Testing
import com.google.testing.model.ClientInfo
import com.google.testing.model.EnvironmentMatrix
import com.google.testing.model.GoogleCloudStorage
import com.google.testing.model.IosTestSetup
import com.google.testing.model.ResultStorage
import com.google.testing.model.TestMatrix
import com.google.testing.model.TestSpecification
import com.google.testing.model.ToolResultsHistory
import ftl.api.TestMatrixIos
import ftl.client.google.GcTesting
import ftl.client.google.run.mapGcsPathsToFileReference
import ftl.client.google.run.mapToIosDeviceFiles
import ftl.client.google.run.toClientInfoDetailList
import ftl.client.google.run.toIosDeviceFile
import ftl.http.executeWithRetry
import ftl.run.exception.FlankGeneralError
import ftl.util.timeoutToSeconds
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun executeIosTests(
    config: TestMatrixIos.Config,
    testMatrixTypes: List<TestMatrixIos.Type>,
): List<TestMatrix> = testMatrixTypes
    .foldIndexed(emptyList<Deferred<TestMatrix>>()) { _, testMatrices, testMatrixType ->
        testMatrices + executeIosTestMatrixAsync(testMatrixType, config)
    }.awaitAll()

private suspend fun executeIosTestMatrixAsync(
    type: TestMatrixIos.Type,
    config: TestMatrixIos.Config
): Deferred<TestMatrix> = coroutineScope {
    async(Dispatchers.IO) {
        createIosTestMatrix(type, config).executeWithRetry()
    }
}

private fun createIosTestMatrix(
    testMatrixType: TestMatrixIos.Type,
    config: TestMatrixIos.Config,
): Testing.Projects.TestMatrices.Create {

    val testMatrix = TestMatrix()
        .setClientInfo(config.clientInfo)
        .setTestSpecification(getIosTestSpecification(testMatrixType, config))
        .setEnvironmentMatrix(config.environmentMatrix)
        .setResultStorage(resultsStorage(config, testMatrixType))
        .setFlakyTestAttempts(config.flakyTestAttempts)
        .setFailFast(config.failFast)

    return runCatching {
        GcTesting.get.projects().testMatrices().create(config.project, testMatrix)
    }.getOrElse { throw FlankGeneralError(it) }
}

private fun resultsStorage(config: TestMatrixIos.Config, type: TestMatrixIos.Type): ResultStorage {
    return ResultStorage().setGoogleCloudStorage(
        GoogleCloudStorage().setGcsPath(type.matrixGcsPath)
    ).setToolResultsHistory(
        ToolResultsHistory().setHistoryId(config.resultsHistoryName).setProjectId(config.project)
    )
}

private val TestMatrixIos.Config.environmentMatrix
    get() = EnvironmentMatrix().setIosDeviceList(GcIosDevice.build(devices))

private fun getIosTestSpecification(
    testMatrixType: TestMatrixIos.Type,
    config: TestMatrixIos.Config
): TestSpecification = TestSpecification()
    .setDisableVideoRecording(!config.recordVideo)
    .setTestTimeout("${timeoutToSeconds(config.testTimeout)}s")
    .setIosTestSetup(getIosTestSetup(config))
    .setupIosTest(testMatrixType)

private fun getIosTestSetup(
    config: TestMatrixIos.Config
): IosTestSetup = IosTestSetup()
    .setNetworkProfile(config.networkProfile)
    .setPushFiles(config.otherFiles.mapToIosDeviceFiles())
    .setAdditionalIpas(config.additionalIpasGcsPaths.mapGcsPathsToFileReference())
    .setPullDirectories(config.directoriesToPull.toIosDeviceFiles())

private fun List<String>.toIosDeviceFiles() = map { path -> toIosDeviceFile(path) }

private val TestMatrixIos.Config.clientInfo
    get() = ClientInfo().setName("Flank").setClientInfoDetails(clientDetails.toClientInfoDetailList())
