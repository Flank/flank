package ftl.gc

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.Account
import com.google.api.services.testing.model.AndroidDeviceList
import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.Apk
import com.google.api.services.testing.model.ClientInfo
import com.google.api.services.testing.model.DeviceFile
import com.google.api.services.testing.model.EnvironmentMatrix
import com.google.api.services.testing.model.EnvironmentVariable
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.GoogleAuto
import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.RegularFile
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.TestSetup
import com.google.api.services.testing.model.TestSpecification
import com.google.api.services.testing.model.ToolResultsHistory
import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import ftl.util.join
import ftl.util.timeoutToSeconds

object GcAndroidTestMatrix {

    private fun Map.Entry<String, String>.toEnvironmentVariable() = EnvironmentVariable().apply {
        key = this@toEnvironmentVariable.key
        value = this@toEnvironmentVariable.value
    }

    fun build(
        appApkGcsPath: String,
        testApkGcsPath: String,
        otherFiles: Map<String, String>,
        runGcsPath: String,
        androidDeviceList: AndroidDeviceList,
        testShards: ShardChunks,
        args: AndroidArgs,
        toolResultsHistory: ToolResultsHistory,
        additionalApkGcsPaths: List<String>
    ): Testing.Projects.TestMatrices.Create {

        // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
        val clientInfo = ClientInfo()
            .setName("Flank")
            .setClientInfoDetails(args.clientDetails?.toClientInfoDetailList())

        val matrixGcsPath = join(args.resultsBucket, runGcsPath)

        val androidInstrumentation = AndroidInstrumentationTest()
            .setAppApk(FileReference().setGcsPath(appApkGcsPath))
            .setTestApk(FileReference().setGcsPath(testApkGcsPath))
            .setupTestTargets(args, testShards)

        if (args.testRunnerClass != null) {
            androidInstrumentation.testRunnerClass = args.testRunnerClass
        }

        if (args.useOrchestrator) {
            androidInstrumentation.orchestratorOption = "USE_ORCHESTRATOR"
        }

        // --auto-google-login
        // https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/android/matrix_creator.py#L174
        var account: Account? = null

        if (args.autoGoogleLogin) {
            account = Account().setGoogleAuto(GoogleAuto())
        }

        val testSetup = TestSetup()
            .setAccount(account)
            .setNetworkProfile(args.networkProfile)
            .setDirectoriesToPull(args.directoriesToPull)
            .setAdditionalApks(additionalApkGcsPaths.mapGcsPathsToApks())
            .setFilesToPush(otherFiles.mapToDeviceFiles())

        if (args.environmentVariables.isNotEmpty()) {
            testSetup.environmentVariables =
                args.environmentVariables.map { it.toEnvironmentVariable() }
        }

        val testTimeoutSeconds = timeoutToSeconds(args.testTimeout)

        val testSpecification = TestSpecification()
            .setAndroidInstrumentationTest(androidInstrumentation)
            .setDisablePerformanceMetrics(!args.performanceMetrics)
            .setDisableVideoRecording(!args.recordVideo)
            .setTestTimeout("${testTimeoutSeconds}s")
            .setTestSetup(testSetup)

        val resultsStorage = ResultStorage()
            .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))
            .setToolResultsHistory(toolResultsHistory)

        val environmentMatrix = EnvironmentMatrix()
            .setAndroidDeviceList(androidDeviceList)

        val testMatrix = TestMatrix()
            .setClientInfo(clientInfo)
            .setTestSpecification(testSpecification)
            .setResultStorage(resultsStorage)
            .setEnvironmentMatrix(environmentMatrix)
            .setFlakyTestAttempts(args.flakyTestAttempts)
        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}

private fun List<String>?.mapGcsPathsToApks(): List<Apk>? = this
    ?.takeIf { it.isNotEmpty() }
    ?.map { gcsPath -> Apk().setLocation(FileReference().setGcsPath(gcsPath)) }

private fun Map<String, String>.mapToDeviceFiles() = map { (devicePath: String, gcsFilePath: String) ->
    DeviceFile().setRegularFile(
        RegularFile()
            .setDevicePath(devicePath)
            .setContent(FileReference().setGcsPath(gcsFilePath))
    )
}
