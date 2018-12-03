package ftl.gc

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.Account
import com.google.api.services.testing.model.AndroidDeviceList
import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.ClientInfo
import com.google.api.services.testing.model.EnvironmentMatrix
import com.google.api.services.testing.model.EnvironmentVariable
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.GoogleAuto
import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.TestSetup
import com.google.api.services.testing.model.TestSpecification
import com.google.api.services.testing.model.ToolResultsHistory
import ftl.args.AndroidArgs
import ftl.util.ShardCounter
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import ftl.util.testTimeoutToSeconds
import ftl.util.validateTestShardIndex

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
        args: AndroidArgs,
        shardCounter: ShardCounter,
        toolResultsHistory: ToolResultsHistory
    ): Testing.Projects.TestMatrices.Create {
        validateTestShardIndex(testShardsIndex, args)

        // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
        val clientInfo = ClientInfo().setName("Flank")

        val matrixGcsPath = join(args.resultsBucket, runGcsPath, shardCounter.next())

        val androidInstrumentation = AndroidInstrumentationTest()
            .setAppApk(FileReference().setGcsPath(appApkGcsPath))
            .setTestApk(FileReference().setGcsPath(testApkGcsPath))

        if (args.useOrchestrator) {
            androidInstrumentation.orchestratorOption = "USE_ORCHESTRATOR"
        }

        androidInstrumentation.testTargets = args.testShardChunks.elementAt(testShardsIndex).toList()

        // --auto-google-login
        // https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/android/matrix_creator.py#L174
        var account: Account? = null

        if (args.autoGoogleLogin) {
            account = Account().setGoogleAuto(GoogleAuto())
        }

        val testSetup = TestSetup()
            .setAccount(account)
            .setDirectoriesToPull(args.directoriesToPull)

        if (args.environmentVariables.isNotEmpty()) {
            testSetup.environmentVariables =
                args.environmentVariables.map { it.toEnvironmentVariable() }
        }

        val testTimeoutSeconds = testTimeoutToSeconds(args.testTimeout)

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
        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
