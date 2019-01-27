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
import com.google.api.services.testing.model.ToolResultsHistory
import ftl.args.IosArgs
import ftl.ios.Xctestrun
import ftl.ios.Xctestrun.toByteArray
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
        args: IosArgs,
        shardCounter: ShardCounter,
        toolResultsHistory: ToolResultsHistory
    ): Testing.Projects.TestMatrices.Create {
        validateTestShardIndex(testShardsIndex, args)
        val clientInfo = ClientInfo().setName("Flank")

        val gcsBucket = args.resultsBucket
        val matrixGcsSuffix = join(runGcsPath, shardCounter.next())
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)

        // Parameterized tests on iOS don't shard correctly.
        // Avoid changing Xctestrun file when disableSharding is on.
        val generatedXctestrun = if (args.disableSharding) {
            xcTestParsed.toByteArray()
        } else {
            val methods = args.testShardChunks.elementAt(testShardsIndex)
            Xctestrun.rewrite(xcTestParsed, methods)
        }

        val xctestrunFileGcsPath = GcStorage.uploadXCTestFile(args, gcsBucket, matrixGcsSuffix, generatedXctestrun)

        val iOSXCTest = IosXcTest()
            .setTestsZip(FileReference().setGcsPath(testZipGcsPath))
            .setXctestrun(FileReference().setGcsPath(xctestrunFileGcsPath))
            .setXcodeVersion(args.xcodeVersion)

        val iOSTestSetup = IosTestSetup()
            .setNetworkProfile(null)

        val testTimeoutSeconds = testTimeoutToSeconds(args.testTimeout)

        val testSpecification = TestSpecification()
            .setDisableVideoRecording(!args.recordVideo)
            .setTestTimeout("${testTimeoutSeconds}s")
            .setIosTestSetup(iOSTestSetup)
            .setIosXcTest(iOSXCTest)

        val resultStorage = ResultStorage()
            .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(matrixGcsPath))
            .setToolResultsHistory(toolResultsHistory)

        val environmentMatrix = EnvironmentMatrix().setIosDeviceList(iosDeviceList)

        val testMatrix = TestMatrix()
            .setClientInfo(clientInfo)
            .setTestSpecification(testSpecification)
            .setEnvironmentMatrix(environmentMatrix)
            .setResultStorage(resultStorage)
            .setFlakyTestAttempts(args.flakyTestAttempts)

        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            fatalError(e)
        }

        throw RuntimeException("Failed to create test matrix")
    }
}
