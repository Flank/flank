package ftl.gc

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.*
import ftl.config.YamlConfig
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import ftl.util.Utils.sleep
import ftl.util.Utils.uniqueObjectName
import java.time.Duration.ofHours
import java.util.concurrent.TimeUnit

object GcTestMatrix {

    fun build(
            appApkGcsPath: String,
            testApkGcsPath: String,
            runGcsPath: String,
            androidMatrix: AndroidMatrix,
            testTargets: List<String>? = null,
            testShardsIndex: Int = -1,
            config: YamlConfig): Testing.Projects.TestMatrices.Create {
        val testShardsTotal = config.testShards
        val useTestShards = testShardsTotal > 1 &&
                testShardsIndex < testShardsTotal

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

        if (testTargets != null && testTargets.isNotEmpty()) {
            androidInstrumentation.testTargets = testTargets
        }

        if (useTestShards) {
            androidInstrumentation.testTargets = config.testShardChunks[testShardsIndex]
        }

        val testTimeoutSeconds = TimeUnit.MINUTES.toSeconds(config.testTimeoutMinutes)

        val testSetup = TestSetup().setDirectoriesToPull(listOf("/sdcard/screenshots"))

        testMatrix.testSpecification = TestSpecification()
                .setAndroidInstrumentationTest(androidInstrumentation)
                .setDisablePerformanceMetrics(config.disablePerformanceMetrics)
                .setDisableVideoRecording(config.disableVideoRecording)
                .setTestTimeout("${testTimeoutSeconds}s")
                .setTestSetup(testSetup)

        val matrixGcsPath = join(config.rootGcsBucket, runGcsPath, uniqueObjectName())
        testMatrix.resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))
        testMatrix.environmentMatrix = EnvironmentMatrix().setAndroidMatrix(androidMatrix)

        try {
            return GcTesting.get.projects().testMatrices().create(config.projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }

//    fun cancel(testMatrixId: String, config: YamlConfig) {
//        try {
//            GcTesting.get.projects().testMatrices().cancel(config.projectId, testMatrixId)
//        } catch (e: Exception) {
//            try {
//                GcTesting.get.projects().testMatrices().cancel(config.projectId, testMatrixId)
//            } catch (e2: Exception) {
//                // :(
//                e2.printStackTrace()
//            }
//        }
//    }

    // Getting the test matrix may throw an internal server error.
    //  {
    //      "code" : 500,
    //      "errors" : [ {
    //      "domain" : "global",
    //      "message" : "Internal error encountered.",
    //      "reason" : "backendError"
    //  } ],
    //      "message" : "Internal error encountered.",
    //      "status" : "INTERNAL"
    //  }
    //
    // Randomly throws errors... yay FTL
    //    com.google.api.client.googleapis.json.GoogleJsonResponseException: 503 Service Unavailable
    //    {
    //        "code" : 503,
    //        "errors" : [ {
    //        "domain" : "global",
    //        "message" : "The service is currently unavailable.",
    //        "reason" : "backendError"
    //    } ],
    //        "message" : "The service is currently unavailable.",
    //        "status" : "UNAVAILABLE"
    //    }
    fun refresh(testMatrixId: String, config: YamlConfig): TestMatrix {
        val getMatrix = GcTesting.get.projects().testMatrices().get(config.projectId, testMatrixId)
        var failed = 0
        val maxWait = ofHours(1).seconds

        while (failed < maxWait) {
            try {
                return getMatrix.execute()
            } catch (e: Exception) {
                sleep(1)
                failed += 1
            }
        }

        throw RuntimeException("failed to refresh matrix")
    }
}
