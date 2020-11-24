package ftl.run.platform

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IosArgs
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.run.IOS_SHARD_FILE
import ftl.run.dumpShards
import ftl.run.model.TestResult
import ftl.run.platform.android.uploadAdditionalIpas
import ftl.run.platform.android.uploadOtherFiles
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.shard.testCases
import ftl.util.ShardCounter
import ftl.util.asFileReference
import ftl.util.uploadIfNeeded
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
internal suspend fun IosArgs.runIosTests(): TestResult = coroutineScope {
    val args = this@runIosTests
    val stopwatch = beforeRunTests()

    val iosDeviceList = GcIosMatrix.build(devices)
    val xcTestParsed = parseToNSDictionary(xctestrunFile)

    val jobs = arrayListOf<Deferred<TestMatrix>>()
    val runCount = repeatTests
    val shardCounter = ShardCounter()
    val history = GcToolResults.createToolResultsHistory(args)
    val otherGcsFiles = uploadOtherFiles()
    val additionalIpasGcsFiles = uploadAdditionalIpas()

    dumpShards()
    if (disableResultsUpload.not())
        GcStorage.upload(IOS_SHARD_FILE, resultsBucket, resultsDir)

    // Upload only after parsing shards to detect missing methods early.
    val xcTestGcsPath = uploadIfNeeded(xctestrunZip.asFileReference()).gcs

    println(beforeRunMessage(testShardChunks))

    repeat(runCount) {
        jobs += testShardChunks.map { testTargets ->
            async(Dispatchers.IO) {
                GcIosTestMatrix.build(
                    iosDeviceList = iosDeviceList,
                    testZipGcsPath = xcTestGcsPath,
                    runGcsPath = resultsDir,
                    testTargets = testTargets.testMethodNames,
                    xcTestParsed = xcTestParsed,
                    args = args,
                    shardCounter = shardCounter,
                    toolResultsHistory = history,
                    otherFiles = otherGcsFiles,
                    additionalIpasGcsPaths = additionalIpasGcsFiles
                ).executeWithRetry()
            }
        }
    }

    TestResult(
        matrixMap = afterRunTests(jobs.awaitAll(), stopwatch),
        shardChunks = testShardChunks.testCases
    )
}
