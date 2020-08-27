package ftl.run.platform

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.ios.Xctestrun
import ftl.run.model.TestResult
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.util.ShardCounter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
internal suspend fun runIosTests(iosArgs: IosArgs): TestResult = coroutineScope {
    val (stopwatch, runGcsPath) = beforeRunTests(iosArgs)

    val iosDeviceList = GcIosMatrix.build(iosArgs.devices)

    val xcTestParsed = Xctestrun.parse(iosArgs.xctestrunFile)

    val jobs = arrayListOf<Deferred<TestMatrix>>()
    val runCount = iosArgs.repeatTests
    val shardCounter = ShardCounter()
    val history = GcToolResults.createToolResultsHistory(iosArgs)

    // Upload only after parsing shards to detect missing methods early.
    val xcTestGcsPath = getXcTestGcPath(iosArgs, runGcsPath)

    println(beforeRunMessage(iosArgs, iosArgs.testShardChunks))

    repeat(runCount) {
        jobs += iosArgs.testShardChunks.map { testTargets ->
            async(Dispatchers.IO) {
                GcIosTestMatrix.build(
                    iosDeviceList = iosDeviceList,
                    testZipGcsPath = xcTestGcsPath,
                    runGcsPath = runGcsPath,
                    testTargets = testTargets.testsList,
                    xcTestParsed = xcTestParsed,
                    args = iosArgs,
                    shardCounter = shardCounter,
                    toolResultsHistory = history
                ).executeWithRetry()
            }
        }
    }

    TestResult(
        matrixMap = afterRunTests(jobs.awaitAll(), runGcsPath, stopwatch, iosArgs),
        shardChunks = iosArgs.testShardChunks.map { it.testsList }
    )
}

private fun getXcTestGcPath(
    iosArgs: IosArgs,
    runGcsPath: String
) = if (iosArgs.xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
    iosArgs.xctestrunZip
} else {
    GcStorage.uploadXCTestZip(iosArgs, runGcsPath)
}
