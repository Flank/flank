package ftl.gc

import com.dd.plist.NSDictionary
import com.google.testing.Testing
import com.google.testing.model.ClientInfo
import com.google.testing.model.EnvironmentMatrix
import com.google.testing.model.FileReference
import com.google.testing.model.GoogleCloudStorage
import com.google.testing.model.IosDeviceList
import com.google.testing.model.IosTestSetup
import com.google.testing.model.IosXcTest
import com.google.testing.model.ResultStorage
import com.google.testing.model.TestMatrix
import com.google.testing.model.TestSpecification
import com.google.testing.model.ToolResultsHistory
import ftl.args.IosArgs
import ftl.gc.android.mapGcsPathsToFileReference
import ftl.gc.android.mapToIosDeviceFiles
import ftl.gc.android.toIosDeviceFile
import ftl.ios.xctest.common.toByteArray
import ftl.ios.xctest.rewriteXcTestRunV1
import ftl.run.exception.FlankGeneralError
import ftl.util.ShardCounter
import ftl.util.join
import ftl.util.timeoutToSeconds

object GcIosTestMatrix {

    @Suppress("LongParameterList")
    fun build(
        iosDeviceList: IosDeviceList,
        testZipGcsPath: String,
        runGcsPath: String,
        xcTestParsed: NSDictionary,
        args: IosArgs,
        testTargets: List<String>,
        shardCounter: ShardCounter,
        toolResultsHistory: ToolResultsHistory,
        otherFiles: Map<String, String>,
        additionalIpasGcsPaths: List<String>
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
            rewriteXcTestRunV1(args.xctestrunFile, testTargets)
        }

        // Add shard number to file name
        val xctestrunNewFileName =
            StringBuilder(args.xctestrunFile).insert(args.xctestrunFile.lastIndexOf("."), "_$shardName").toString()

        val xctestrunFileGcsPath =
            GcStorage.uploadXCTestFile(xctestrunNewFileName, gcsBucket, matrixGcsSuffix, generatedXctestrun)

        val iOSXCTest = IosXcTest()
            .setTestsZip(FileReference().setGcsPath(testZipGcsPath))
            .setXctestrun(FileReference().setGcsPath(xctestrunFileGcsPath))
            .setXcodeVersion(args.xcodeVersion)
            .setTestSpecialEntitlements(args.testSpecialEntitlements)

        val iOSTestSetup = IosTestSetup()
            .setNetworkProfile(args.networkProfile)
            .setPushFiles(otherFiles.mapToIosDeviceFiles())
            .setAdditionalIpas(additionalIpasGcsPaths.mapGcsPathsToFileReference())
            .setPullDirectories(args.directoriesToPull.toIosDeviceFiles())

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
            .setFailFast(args.failFast)

        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            throw FlankGeneralError(e)
        }
    }
}

private fun List<String>.toIosDeviceFiles() = map { path -> toIosDeviceFile(path) }
