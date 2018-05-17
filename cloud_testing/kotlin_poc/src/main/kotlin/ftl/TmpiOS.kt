package ftl

import com.google.api.services.testing.model.*
import ftl.config.YamlConfig
import ftl.gc.GcTesting
import ftl.util.Utils
import java.io.File
import java.util.concurrent.TimeUnit

object TmpiOS {
    @JvmStatic
    fun main(args: Array<String>) {
        // todo: port iOS features from
        // https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
        // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
        // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/

        val xctestZipFile = "./fixtures/EarlGreyExampleSwiftTests.zip"
        if (!File(xctestZipFile).exists()) throw RuntimeException("zip doesn't exist!")

        // TODO: auto create root bucket if it doesn't exist
        val config = YamlConfig(
                projectId = "delta-essence-114723",
                rootGcsBucket = "earlgrey-swift",
                xctestZip = xctestZipFile,
                disablePerformanceMetrics = false,
                disableVideoRecording = false,
                testTimeoutMinutes = 5
        )

        val testTimeoutSeconds = TimeUnit.MINUTES.toSeconds(config.testTimeoutMinutes)

        val runGcsPath = Utils.uniqueObjectName()
//        val xcTestGcsPath = GcStorage.uploadXCTestZip(config, runGcsPath)
        val xcTestGcsPath = "gs://earlgrey-swift/2018-05-17_16:46:36.711000_MMtK/EarlGreyExampleSwiftTests.zip"

        val iOSXCTest = IosXcTest()
                .setTestsZip(FileReference().setGcsPath(xcTestGcsPath))

        val iOSTestSetup = IosTestSetup()
                .setNetworkProfile(null)

        val testSpec = TestSpecification()
//                .setDisablePerformanceMetrics(config.disablePerformanceMetrics)
                .setDisableVideoRecording(config.disableVideoRecording)
                .setTestTimeout("${testTimeoutSeconds}s")
                .setIosTestSetup(iOSTestSetup)
                .setIosXcTest(iOSXCTest)

        val matrixGcsPath = Utils.join(config.rootGcsBucket, runGcsPath, Utils.uniqueObjectName())

        // note:  500 Internal Server Error is returned on invalid model id/version
        // TODO: implement client side model id/version/locacle/orientation validation
        val iOSDevice = IosDevice()
                .setIosModelId("iphone8")
                .setIosVersionId("11.2")
                .setLocale("en_US")
                .setOrientation("portrait")

        val resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))
        val environmentMatrix = EnvironmentMatrix()
                .setIosDeviceList(
                        IosDeviceList().setIosDevices(listOf(iOSDevice)))

        val testMatrix = TestMatrix()
                .setTestSpecification(testSpec)
                .setClientInfo(ClientInfo().setName("Flank"))
                .setEnvironmentMatrix(environmentMatrix)
                .setResultStorage(resultStorage)

        val testRequest = GcTesting.get.projects()
                .testMatrices()
                .create(config.projectId, testMatrix)
        testRequest.execute()
    }
}
