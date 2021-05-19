package ftl.run.platform

import flank.common.join
import flank.common.logLn
import ftl.adapter.google.toApiModel
import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.args.IosArgs
import ftl.args.isXcTest
import ftl.args.shardsFilePath
import ftl.client.google.GcToolResults
import ftl.client.google.run.ios.GcIosDevice
import ftl.config.FtlConstants
import ftl.gc.GcIosTestMatrix
import ftl.http.executeWithRetry
import ftl.ios.xctest.flattenShardChunks
import ftl.run.dumpShards
import ftl.run.model.TestResult
import ftl.run.platform.android.uploadAdditionalIpas
import ftl.run.platform.android.uploadOtherFiles
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.run.platform.ios.createIosTestContexts
import ftl.shard.testCases
import ftl.util.repeat
import ftl.util.saveToFlankLinks
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

// https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
internal suspend fun IosArgs.runIosTests(): TestResult = coroutineScope {
    val args = this@runIosTests
    val stopwatch = beforeRunTests()

    val iosDeviceList = GcIosDevice.build(devices)

    val history = GcToolResults.createToolResultsHistory(args)
    val otherGcsFiles = uploadOtherFiles()
    val additionalIpasGcsFiles = uploadAdditionalIpas()

    dumpShardsIfXcTest()
    saveToFlankLinks(
        shardsFilePath,
        FtlConstants.GCS_STORAGE_LINK + join(resultsBucket, resultsDir)
    )
    val testShardChunks = xcTestRunData.flattenShardChunks()
    logLn(beforeRunMessage(testShardChunks))

    val result = createIosTestContexts().map { context ->
        GcIosTestMatrix.build(
            iosDeviceList = iosDeviceList,
            args = args,
            iosTestContext = context,
            toolResultsHistory = history,
            otherFiles = otherGcsFiles,
            additionalIpasGcsPaths = additionalIpasGcsFiles
        )
    }.repeat(repeatTests)
        .map { async(Dispatchers.IO) { it.executeWithRetry() } }
        .toList()
        .awaitAll()

    TestResult(
        matrixMap = afterRunTests(
            testMatrices = result.map { it.toApiModel() },
            stopwatch = stopwatch
        ),
        shardChunks = testShardChunks.testCases
    )
}

private fun IosArgs.dumpShardsIfXcTest() = takeIf { isXcTest }?.let {
    dumpShards(
        shardFilePath = shardsFilePath
    )
    if (disableResultsUpload.not())
        uploadToRemoteStorage(
            RemoteStorage.Dir(resultsBucket, resultsDir),
            RemoteStorage.Data(shardsFilePath, Files.readAllBytes(Paths.get(shardsFilePath)))
        )
}
