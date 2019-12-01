package ftl.gc

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.*
import ftl.args.AndroidArgs
import ftl.util.ShardCounter
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import ftl.util.testTimeoutToSeconds

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
        testTargets: List<List<String>>,
        args: AndroidArgs,
        shardCounter: ShardCounter,
        toolResultsHistory: ToolResultsHistory
    ): Testing.Projects.TestMatrices.Create {

        // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
        val clientInfo = ClientInfo().setName("Flank")

        val matrixGcsPath = join(args.resultsBucket, runGcsPath, shardCounter.next())

        // ShardingOption().setUniformSharding(UniformSharding().setNumShards())
        val testTargetsForShard: List<TestTargetsForShard> = testTargets.map {
            TestTargetsForShard().setTestTargets(it)
        }
        val manualSharding = ManualSharding().setTestTargetsForShard(testTargetsForShard)
        val shardingOption = ShardingOption().setManualSharding(manualSharding)

        val androidInstrumentation = AndroidInstrumentationTest()
            .setAppApk(FileReference().setGcsPath(appApkGcsPath))
            .setTestApk(FileReference().setGcsPath(testApkGcsPath))
            .setShardingOption(shardingOption)

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
            .setFlakyTestAttempts(args.flakyTestAttempts)
        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
