package ftl.run.platform

import flank.common.join
import flank.common.logLn
import ftl.adapter.google.toApiModel
import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.args.IosArgs
import ftl.args.isXcTest
import ftl.args.shardsFilePath
import ftl.client.google.run.ios.executeIosTests
import ftl.config.FtlConstants
import ftl.ios.xctest.flattenShardChunks
import ftl.run.dumpShards
import ftl.run.exception.FlankGeneralError
import ftl.run.model.TestResult
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.run.platform.ios.createIosTestConfig
import ftl.run.platform.ios.createIosTestContexts
import ftl.run.platform.ios.createIosTestMatrixType
import ftl.shard.testCases
import ftl.util.repeat
import ftl.util.saveToFlankLinks
import java.nio.file.Files
import java.nio.file.Paths
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

// https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
internal suspend fun IosArgs.runIosTests(): TestResult = coroutineScope {
    val args = this@runIosTests
    val stopwatch = beforeRunTests()

    dumpShardsIfXcTest()
    saveToFlankLinks(
        shardsFilePath,
        FtlConstants.GCS_STORAGE_LINK + join(resultsBucket, resultsDir)
    )
    val testShardChunks = xcTestRunData.flattenShardChunks()
    logLn(beforeRunMessage(testShardChunks))

    val result = createIosTestContexts()
        .map { context -> createIosTestMatrixType(context) }
        .repeat(repeatTests)
        .run { executeIosTests(createIosTestConfig(args), toList()) }
        .takeIf { it.isNotEmpty() } ?: throw FlankGeneralError("There are no iOS tests to run")

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
