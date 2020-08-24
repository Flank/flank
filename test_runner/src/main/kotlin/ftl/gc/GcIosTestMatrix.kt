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
import ftl.run.exception.FlankGeneralError
import ftl.util.ShardCounter
import ftl.util.join
import ftl.util.timeoutToSeconds

object GcIosTestMatrix {

    fun build(
        iosDeviceList: IosDeviceList,
        testZipGcsPath: String,
        runGcsPath: String,
        xcTestParsed: NSDictionary,
        args: IosArgs,
        testTargets: List<String>,
        shardCounter: ShardCounter,
        toolResultsHistory: ToolResultsHistory
    ): Testing.Projects.TestMatrices.Create {
        val clientInfo = ClientInfo()
            .setName("Flank")
            .setClientInfoDetails(args.clientDetails?.toClientInfoDetailList())

        val gcsBucket = args.resultsBucket
        val shardName = shardCounter.next()
        val matrixGcsSuffix = join(runGcsPath, shardName)
        val matrixGcsPath = join(gcsBucket, matrixGcsSuffix)

        // Parameterized tests on iOS don't shard correctly.
        // Avoid changing Xctestrun file when disableSharding is on.
        val generatedXctestrun = if (args.disableSharding) {
            xcTestParsed.toByteArray()
        } else {
            Xctestrun.rewrite(xcTestParsed, testTargets)
        }

        // Add shard number to file name
        val xctestrunNewFileName = StringBuilder(args.xctestrunFile).insert(args.xctestrunFile.lastIndexOf("."), "_$shardName").toString()

        val xctestrunFileGcsPath = GcStorage.uploadXCTestFile(xctestrunNewFileName, gcsBucket, matrixGcsSuffix, generatedXctestrun)

        val iOSXCTest = IosXcTest()
            .setTestsZip(FileReference().setGcsPath(testZipGcsPath))
            .setXctestrun(FileReference().setGcsPath(xctestrunFileGcsPath))
            .setXcodeVersion(args.xcodeVersion)

        val iOSTestSetup = IosTestSetup()
            .setNetworkProfile(args.networkProfile)

        val testTimeoutSeconds = timeoutToSeconds(args.testTimeout)

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
            throw FlankGeneralError(e)
        }
    }
}
