package ftl.gc

import com.google.testing.Testing
import com.google.testing.model.Account
import com.google.testing.model.AndroidDeviceList
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
import ftl.args.AndroidArgs
import ftl.args.isDontAutograntPermissions
import ftl.gc.android.mapGcsPathsToApks
import ftl.gc.android.mapToDeviceFiles
import ftl.gc.android.mapToDeviceObbFiles
import ftl.gc.android.setEnvironmentVariables
import ftl.gc.android.setupAndroidTest
import ftl.run.exception.FlankGeneralError
import ftl.run.platform.android.AndroidTestConfig
import ftl.util.timeoutToSeconds

object GcAndroidTestMatrix {

    @Suppress("LongParameterList")
    fun build(
        androidTestConfig: AndroidTestConfig,
        otherFiles: Map<String, String>,
        runGcsPath: String,
        androidDeviceList: AndroidDeviceList,
        args: AndroidArgs,
        toolResultsHistory: ToolResultsHistory,
        additionalApkGcsPaths: List<String>,
        obbFiles: Map<String, String>
    ): Testing.Projects.TestMatrices.Create {
        // https://github.com/bootstraponline/studio-google-cloud-testing/blob/203ed2890c27a8078cd1b8f7ae12cf77527f426b/firebase-testing/src/com/google/gct/testing/launcher/CloudTestsLauncher.java#L120
        val clientInfo = if (androidTestConfig is AndroidTestConfig.Instrumentation && androidTestConfig.clientDetails.isNotEmpty()) {
                            ClientInfo()
                                .setName("Flank")
                                .setClientInfoDetails(androidTestConfig.clientDetails.toClientInfoDetailList())
                        } else {
                            ClientInfo()
                                .setName("Flank")
                                .setClientInfoDetails(args.clientDetails?.toClientInfoDetailList())
                        }
        val matrixGcsPath = join(args.resultsBucket, runGcsPath)

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
            .setFilesToPush(otherFiles.mapToDeviceFiles() + obbFiles.mapToDeviceObbFiles(args.obbNames))
            .setEnvironmentVariables(args, androidTestConfig)
            .setDontAutograntPermissions(args.isDontAutograntPermissions)

        val testTimeoutSeconds = timeoutToSeconds(args.testTimeout)

        val testSpecification = TestSpecification()
            .setDisablePerformanceMetrics(!args.performanceMetrics)
            .setDisableVideoRecording(!args.recordVideo)
            .setTestTimeout("${testTimeoutSeconds}s")
            .setTestSetup(testSetup)
            .setupAndroidTest(androidTestConfig)

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
            .setFailFast(args.failFast)
        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            throw FlankGeneralError(e)
        }
    }
}
