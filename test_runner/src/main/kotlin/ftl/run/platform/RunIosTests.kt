package ftl.run.platform

import com.google.testing.model.TestMatrix
import flank.common.logLn
import ftl.args.IosArgs
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.ios.xctest.flattenShardChunks
import ftl.run.IOS_SHARD_FILE
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

// https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
internal suspend fun IosArgs.runIosTests(): TestResult =
    coroutineScope {
        val args = this@runIosTests
        val stopwatch = beforeRunTests()

        val iosDeviceList = GcIosMatrix.build(devices)

        val history = GcToolResults.createToolResultsHistory(args)
        val otherGcsFiles = uploadOtherFiles()
        val additionalIpasGcsFiles = uploadAdditionalIpas()

        dumpShards()
        if (disableResultsUpload.not())
            GcStorage.upload(IOS_SHARD_FILE, resultsBucket, resultsDir)

        val testShardChunks = xcTestRunData.flattenShardChunks()
        logLn(beforeRunMessage(testShardChunks))

        val result = createIosTestContexts().map { context ->
            GcIosTestMatrix.build(
                iosDeviceList = iosDeviceList,
                args = args,
                testContext = context,
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
                testMatrices = result,
                stopwatch = stopwatch
            ),
            shardChunks = testShardChunks.testCases
        )
    }
