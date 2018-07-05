package ftl.gc

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.*
import ftl.config.AndroidConfig
import ftl.config.YamlConfig
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import ftl.util.Utils.uniqueObjectName
import java.util.concurrent.TimeUnit

object GcAndroidTestMatrix {

    private fun Map.Entry<String, String>.toEnvironmentVariable() = EnvironmentVariable().apply {
        key = this@toEnvironmentVariable.key
        value = this@toEnvironmentVariable.value
    }

    fun build(
            appApkGcsPath: String,
            testApkGcsPath: String,
            runGcsPath: String,
            androidDeviceList: AndroidDeviceList,
            testShardsIndex: Int = -1,
            config: YamlConfig<AndroidConfig>): Testing.Projects.TestMatrices.Create {
        val testShardsTotal = config.flankConfig.testShardChunks.size

        if (testShardsIndex >= testShardsTotal) {
            throw RuntimeException("Invalid test shard index $testShardsIndex not < $testShardsTotal")
        }

        // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
        val testMatrix = TestMatrix()
        testMatrix.clientInfo = ClientInfo().setName("Flank")

        // Use default instrumentationTestRunner
        //   String instrumentationTestRunner = "";
        //   .setTestRunnerClass(instrumentationTestRunner)

        val androidInstrumentation = AndroidInstrumentationTest()
                .setAppApk(FileReference().setGcsPath(appApkGcsPath))
                .setTestApk(FileReference().setGcsPath(testApkGcsPath))

        if (config.gCloudConfig.useOrchestrator) {
            androidInstrumentation.orchestratorOption = "USE_ORCHESTRATOR"
        }

        androidInstrumentation.testTargets = config.flankConfig.testShardChunks.elementAt(testShardsIndex).toList()

        val timeout = config.gCloudConfig.testTimeout
        val testTimeoutSeconds = when {
            timeout.contains("h") -> TimeUnit.HOURS.toSeconds(timeout.removeSuffix("h").toLong()) // Hours
            timeout.contains("m") -> TimeUnit.MINUTES.toSeconds(timeout.removeSuffix("m").toLong()) // Minutes
            timeout.contains("s") -> timeout.removeSuffix("s").toLong() // Seconds
            else -> timeout.removeSuffix("s").toLong() // Seconds
        }

        // --auto-google-login
        // https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/android/matrix_creator.py#L174
        var account: Account? = null

        if (config.gCloudConfig.autoGoogleLogin) {
            account = Account().setGoogleAuto(GoogleAuto())
        }

        val testSetup = TestSetup()
                .setAccount(account)

        testSetup.directoriesToPull = config.gCloudConfig.directoriesToPull

        if (config.gCloudConfig.environmentVariables.isNotEmpty()) {
            testSetup.environmentVariables =
                    config.gCloudConfig.environmentVariables.map { it.toEnvironmentVariable() }
        }

        testMatrix.testSpecification = TestSpecification()
                .setAndroidInstrumentationTest(androidInstrumentation)
                .setDisablePerformanceMetrics(!config.gCloudConfig.disablePerformanceMetrics)
                .setDisableVideoRecording(!config.gCloudConfig.disableRecordVideo)
                .setTestTimeout("${testTimeoutSeconds}s")
                .setTestSetup(testSetup)

        val matrixGcsPath = join(config.getGcsBucket(), runGcsPath, uniqueObjectName())
        testMatrix.resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))
        testMatrix.environmentMatrix = EnvironmentMatrix().setAndroidDeviceList(androidDeviceList)

        try {
            return GcTesting.get.projects().testMatrices().create(config.gCloudConfig.projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
