package ftl.gc

import com.dd.plist.NSDictionary
import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.ClientInfo
import com.google.api.services.testing.model.EnvironmentMatrix
import com.google.api.services.testing.model.FileReference
import com.google.api.services.testing.model.GoogleCloudStorage
import com.google.api.services.testing.model.IosDeviceList
import com.google.api.services.testing.model.IosTestSetup
import com.google.api.services.testing.model.IosXcTest
import com.google.api.services.testing.model.ResultStorage
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.TestSpecification
import ftl.args.IosArgs
import ftl.ios.Xctestrun
import ftl.util.ShardCounter
import ftl.util.Utils.fatalError
import ftl.util.Utils.join
import ftl.util.testTimeoutToSeconds
import ftl.util.validateTestShardIndex

object GcIosTestMatrix {

    fun build(
        iosDeviceList: IosDeviceList,
        testZipGcsPath: String,
        runGcsPath: String,
        testShardsIndex: Int,
        xcTestParsed: NSDictionary,
        config: IosArgs,
        shardCounter: ShardCounter
    ): Testing.Projects.TestMatrices.Create {
        validateTestShardIndex(testShardsIndex, config)

        val gcsBucket = config.resultsBucket
        val matrixGcsSuffix = join(runGcsPath, shardCounter.next())
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)
        val methods = config.testShardChunks.elementAt(testShardsIndex)

        val generatedXctestrun = Xctestrun.rewrite(xcTestParsed, methods)
        val xctestrunFileGcsPath = GcStorage.uploadXCTestFile(config, gcsBucket, matrixGcsSuffix, generatedXctestrun)

        val iOSXCTest = IosXcTest()
            .setTestsZip(FileReference().setGcsPath(testZipGcsPath))
            .setXctestrun(FileReference().setGcsPath(xctestrunFileGcsPath))

        val iOSTestSetup = IosTestSetup()
            .setNetworkProfile(null)

        val testTimeoutSeconds = testTimeoutToSeconds(config.testTimeout)

        val testSpec = TestSpecification()
            .setDisableVideoRecording(!config.recordVideo)
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
            return GcTesting.get.projects().testMatrices().create(config.projectId, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
