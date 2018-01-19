package ftl

import com.google.common.collect.Lists
import com.google.testing.Testing
import com.google.testing.model.*
import ftl.Constants.projectId
import ftl.GlobalConfig.bucketGcsPath
import ftl.Utils.fatalError
import ftl.Utils.uniqueObjectName
import java.util.concurrent.TimeUnit

object GcTestMatrix {

    fun build(
            appApkGcsPath: String,
            testApkGcsPath: String,
            androidMatrix: AndroidMatrix,
            testTargets: String? = null,
            runConfig: RunConfig): Testing.Projects.TestMatrices.Create {
        // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
        val testMatrix = TestMatrix()
        testMatrix.clientInfo = ClientInfo().setName("Flank")

        // Use default instrumentationTestRunner
        //   String instrumentationTestRunner = "";
        //   .setTestRunnerClass(instrumentationTestRunner)

        val androidInstrumentation = AndroidInstrumentationTest()
                .setAppApk(FileReference().setGcsPath(appApkGcsPath))
                .setTestApk(FileReference().setGcsPath(testApkGcsPath))
                .setOrchestratorOption(runConfig.useOrchestrator)

        if (testTargets != null) {
            androidInstrumentation.testTargets = Lists.newArrayList(testTargets)
        }

        val testTimeoutSeconds = TimeUnit.MINUTES.toSeconds(runConfig.testTimeoutMinutes)

        testMatrix.testSpecification = TestSpecification()
                .setAndroidInstrumentationTest(androidInstrumentation)
                .setDisablePerformanceMetrics(runConfig.disablePerformanceMetrics)
                .setDisableVideoRecording(runConfig.disableVideoRecording)
                .setTestTimeout("${testTimeoutSeconds}s")

        testMatrix.resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(bucketGcsPath + "/" + uniqueObjectName()))
        testMatrix.environmentMatrix = EnvironmentMatrix().setAndroidMatrix(androidMatrix)

        try {
            return GcTesting.get()!!.projects().testMatrices().create(projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }

    fun cancel(testMatrixId: String) {
        try {
            GcTesting.get()!!.projects().testMatrices().cancel(projectId, testMatrixId)
        } catch (e: Exception) {
            try {
                GcTesting.get()!!.projects().testMatrices().cancel(projectId, testMatrixId)
            } catch (e2: Exception) {
                // :(
                e2.printStackTrace()
            }
        }
    }

    fun refresh(testMatrixId: String): TestMatrix {
        try {
            return GcTesting.get()!!.projects().testMatrices().get(projectId, testMatrixId).execute()
        } catch (e: Exception) {
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
            fatalError(e)
        }

        throw RuntimeException("Failed to refresh")
    }
}
