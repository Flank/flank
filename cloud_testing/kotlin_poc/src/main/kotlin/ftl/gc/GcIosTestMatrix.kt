package ftl.gc

import com.dd.plist.NSDictionary
import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.*
import ftl.config.IosConfig
import ftl.config.YamlConfig
import ftl.ios.Xctestrun
import ftl.util.Utils
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import java.util.concurrent.TimeUnit

object GcIosTestMatrix {

    fun build(
            iosDeviceList: IosDeviceList,
            testZipGcsPath: String,
            runGcsPath: String,
            testShardsIndex: Int,
            xcTestParsed: NSDictionary,
            config: YamlConfig<IosConfig>): Testing.Projects.TestMatrices.Create {

        val gcsBucket = config.getGcsBucket()
        val matrixGcsSuffix = join(runGcsPath, Utils.uniqueObjectName())
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)
        val methods = config.flankConfig.testShardChunks.elementAt(testShardsIndex)

        val generatedXctestrun = Xctestrun.rewrite(xcTestParsed, methods)
        val xctestrunFileGcsPath = GcStorage.uploadXCTestFile(config.gCloudConfig, gcsBucket, matrixGcsSuffix, generatedXctestrun)

        val iOSXCTest = IosXcTest()
                .setTestsZip(FileReference().setGcsPath(testZipGcsPath))
                .setXctestrun(FileReference().setGcsPath(xctestrunFileGcsPath))

        val iOSTestSetup = IosTestSetup()
                .setNetworkProfile(null)

        val timeout = config.gCloudConfig.testTimeout
        val testTimeoutSeconds = when {
            timeout.contains("h") -> TimeUnit.HOURS.toSeconds(timeout.removeSuffix("h").toLong()) // Hours
            timeout.contains("m") -> TimeUnit.MINUTES.toSeconds(timeout.removeSuffix("m").toLong()) // Minutes
            timeout.contains("s") -> timeout.removeSuffix("s").toLong() // Seconds
            else -> timeout.removeSuffix("s").toLong() // Seconds
        }

        val testSpec = TestSpecification()
                .setDisablePerformanceMetrics(!config.gCloudConfig.performanceMetrics)
                .setDisableVideoRecording(!config.gCloudConfig.recordVideo)
                .setTestTimeout("${testTimeoutSeconds}s")
                .setIosTestSetup(iOSTestSetup)
                .setIosXcTest(iOSXCTest)


        val resultStorage = ResultStorage()
                .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))

        val environmentMatrix = EnvironmentMatrix().setIosDeviceList(iosDeviceList)

        val testMatrix = TestMatrix()
                .setTestSpecification(testSpec)
                .setClientInfo(ClientInfo().setName("Flank"))
                .setEnvironmentMatrix(environmentMatrix)
                .setResultStorage(resultStorage)

        try {
            return GcTesting.get.projects().testMatrices().create(config.gCloudConfig.projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
