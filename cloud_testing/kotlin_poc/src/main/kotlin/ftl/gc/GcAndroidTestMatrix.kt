package ftl.gc

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.*
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.StorageClass
import com.google.cloud.storage.StorageOptions
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
            config: YamlConfig): Testing.Projects.TestMatrices.Create {
        val testShardsTotal = config.testShardChunks.size

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

        if (config.useOrchestrator) {
            androidInstrumentation.orchestratorOption = "USE_ORCHESTRATOR"
        }

        androidInstrumentation.testTargets = config.testShardChunks.elementAt(testShardsIndex).toList()

        val testTimeoutSeconds = TimeUnit.MINUTES.toSeconds(config.testTimeoutMinutes)

        // --auto-google-login
        // https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/android/matrix_creator.py#L174
        var account: Account? = null

        if (config.autoGoogleLogin) {
            account = Account().setGoogleAuto(GoogleAuto())
        }

        val testSetup = TestSetup()
                .setAccount(account)

        testSetup.directoriesToPull = config.directoriesToPull

        if (config.environmentVariables.isNotEmpty()) {
            testSetup.environmentVariables =
                    config.environmentVariables.map { it.toEnvironmentVariable() }
        }

        testMatrix.testSpecification = TestSpecification()
                .setAndroidInstrumentationTest(androidInstrumentation)
                .setDisablePerformanceMetrics(config.disablePerformanceMetrics)
                .setDisableVideoRecording(config.disableVideoRecording)
                .setTestTimeout("${testTimeoutSeconds}s")
                .setTestSetup(testSetup)

        val matrixGcsPath = join(config.getGcsBucket(), runGcsPath, uniqueObjectName())
        testMatrix.resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))
        testMatrix.environmentMatrix = EnvironmentMatrix().setAndroidDeviceList(androidDeviceList)

        try {
            return GcTesting.getTestingWithId(config.projectId).projects().testMatrices().create(config.projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
