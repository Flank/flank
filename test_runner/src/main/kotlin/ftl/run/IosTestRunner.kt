package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.ios.Xctestrun
import ftl.json.MatrixMap
import ftl.run.GenericTestRunner.beforeRunMessage
import ftl.util.ShardCounter
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object IosTestRunner {
    // https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
    suspend fun runTests(iosArgs: IosArgs): MatrixMap {
        val (stopwatch, runGcsPath) = GenericTestRunner.beforeRunTests()

        val xcTestGcsPath = if (iosArgs.xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            iosArgs.xctestrunZip
        } else {
            GcStorage.uploadXCTestZip(iosArgs, runGcsPath)
        }

        val iosDeviceList = GcIosMatrix.build(iosArgs.devices)

        val xcTestParsed = Xctestrun.parse(iosArgs.xctestrunFile)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = iosArgs.repeatTests
        val deviceCount = iosArgs.testShardChunks.size
        val shardCounter = ShardCounter()
        val history = GcToolResults.createToolResultsHistory(iosArgs)

        println(beforeRunMessage(iosArgs))
        repeat(runCount) {
            repeat(deviceCount) { testShardsIndex ->
                jobs += async {
                    GcIosTestMatrix.build(
                        iosDeviceList = iosDeviceList,
                        testZipGcsPath = xcTestGcsPath,
                        runGcsPath = runGcsPath,
                        testShardsIndex = testShardsIndex,
                        xcTestParsed = xcTestParsed,
                        config = iosArgs,
                        shardCounter = shardCounter,
                        toolResultsHistory = history
                    ).execute()
                }
            }
        }

        return GenericTestRunner.afterRunTests(jobs, runGcsPath, stopwatch, iosArgs)
    }
}
