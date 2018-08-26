package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.ios.Xctestrun
import ftl.json.MatrixMap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object IosTestRunner {
    // https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
    suspend fun runTests(yamlConfig: IosArgs): MatrixMap {
        val (stopwatch, runGcsPath) = GenericTestRunner.beforeRunTests()

        val xcTestGcsPath = if (yamlConfig.xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            yamlConfig.xctestrunZip
        } else {
            GcStorage.uploadXCTestZip(yamlConfig, runGcsPath)
        }

        val iosDeviceList = GcIosMatrix.build(yamlConfig.devices)

        val xcTestParsed = Xctestrun.parse(yamlConfig.xctestrunFile)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = yamlConfig.testRuns
        val repeatShard = yamlConfig.testShardChunks.size
        val testsPerVm = yamlConfig.testShardChunks.first().size
        val testsTotal = yamlConfig.testShardChunks.sumBy { it.size }

        println("  Running ${runCount}x using $repeatShard VMs per run. ${runCount * repeatShard} total VMs")
        println("  $testsPerVm tests per VM. $testsTotal total tests per run")
        repeat(runCount) {
            repeat(repeatShard) { testShardsIndex ->
                jobs += async {
                    GcIosTestMatrix.build(
                            iosDeviceList = iosDeviceList,
                            testZipGcsPath = xcTestGcsPath,
                            runGcsPath = runGcsPath,
                            testShardsIndex = testShardsIndex,
                            xcTestParsed = xcTestParsed,
                            config = yamlConfig).execute()
                }
            }
        }

        return GenericTestRunner.afterRunTests(jobs, runGcsPath, stopwatch, yamlConfig)
    }
}
