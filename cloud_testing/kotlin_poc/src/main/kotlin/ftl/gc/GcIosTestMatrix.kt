package ftl.gc

import com.dd.plist.NSDictionary
import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.*
import ftl.config.YamlConfig
import ftl.util.Utils
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import xctest.Xctestrun
import java.util.concurrent.TimeUnit

object GcIosTestMatrix {

    fun build(
            iosDevice: IosDevice,
            testZipGcsPath: String,
            runGcsPath: String,
            testShardsIndex: Int,
            xcTestParsed: NSDictionary,
            config: YamlConfig): Testing.Projects.TestMatrices.Create {

        val matrixGcsSuffix = join(runGcsPath, Utils.uniqueObjectName())
        val matrixGcsPath = join(config.rootGcsBucket, matrixGcsSuffix)
        val methods = config.testShardChunks.elementAt(testShardsIndex)

        val generatedXctestrun = Xctestrun.rewrite(xcTestParsed, methods)
        val xctestrunFileGcsPath = GcStorage.uploadXCTestFile(config, matrixGcsSuffix, generatedXctestrun)

        val iOSXCTest = IosXcTest()
                .setTestsZip(FileReference().setGcsPath(testZipGcsPath))
                .setXctestrun(FileReference().setGcsPath(xctestrunFileGcsPath))

        val iOSTestSetup = IosTestSetup()
                .setNetworkProfile(null)

        val testTimeoutSeconds = TimeUnit.MINUTES.toSeconds(config.testTimeoutMinutes)
        val testSpec = TestSpecification()
                .setDisablePerformanceMetrics(config.disablePerformanceMetrics)
                .setDisableVideoRecording(config.disableVideoRecording)
                .setTestTimeout("${testTimeoutSeconds}s")
                .setIosTestSetup(iOSTestSetup)
                .setIosXcTest(iOSXCTest)


        val resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))

        val environmentMatrix = EnvironmentMatrix()
                .setIosDeviceList(
                        IosDeviceList().setIosDevices(listOf(iosDevice)))

        val testMatrix = TestMatrix()
                .setTestSpecification(testSpec)
                .setClientInfo(ClientInfo().setName("Flank"))
                .setEnvironmentMatrix(environmentMatrix)
                .setResultStorage(resultStorage)

        try {
            return GcTesting.get.projects().testMatrices().create(config.projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
