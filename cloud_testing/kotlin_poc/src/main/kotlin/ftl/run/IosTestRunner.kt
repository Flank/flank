package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.config.FtlConstants
import ftl.config.IosConfig
import ftl.config.YamlConfig
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.ios.Xctestrun
import ftl.json.MatrixMap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object IosTestRunner : GenericTestRunner<IosConfig> {
    // https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
    override suspend fun runTests(yamlConfig: YamlConfig<IosConfig>): MatrixMap {
        val (stopwatch, runGcsPath) = beforeRunTests()

        val xcTestGcsPath = if (yamlConfig.gCloudConfig.xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            yamlConfig.gCloudConfig.xctestrunZip
        } else {
            GcStorage.uploadXCTestZip(yamlConfig.gCloudConfig, runGcsPath)
        }

        val iosDeviceList = GcIosMatrix.build(yamlConfig.gCloudConfig.devices)

        val xcTestParsed = Xctestrun.parse(yamlConfig.gCloudConfig.xctestrunFile)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = yamlConfig.flankConfig.testRuns
        val repeatShard = yamlConfig.flankConfig.testShardChunks.size
        val testsPerVm = yamlConfig.flankConfig.testShardChunks.first().size
        val testsTotal = yamlConfig.flankConfig.testShardChunks.sumBy { it.size }

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

        return afterRunTests(jobs, runGcsPath, stopwatch, yamlConfig)
    }
}
