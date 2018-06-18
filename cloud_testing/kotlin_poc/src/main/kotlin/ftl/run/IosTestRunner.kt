package ftl.run

import com.google.api.services.testing.model.IosDevice
import com.google.api.services.testing.model.TestMatrix
import ftl.config.YamlConfig
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.ios.IosCatalog
import ftl.json.MatrixMap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import xctest.Xctestrun

object IosTestRunner : GenericTestRunner {

    // https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
    // https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
    override suspend fun runTests(config: YamlConfig): MatrixMap {
        val (stopwatch, runGcsPath) = beforeRunTests()

        val xcTestGcsPath = if (config.xctestrunZip.startsWith("gs://")) {
            config.xctestrunZip
        } else {
            GcStorage.uploadXCTestZip(config, runGcsPath)
        }

        val iosDevice = IosDevice()
                .setIosModelId("iphone8")
                .setIosVersionId("11.2")
                .setLocale("en_US") // FTL iOS doesn't currently support other locales or orientations
                .setOrientation("portrait")

        val xcTestParsed = Xctestrun.parse(config.xctestrunFile)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = config.testRuns
        val repeatShard = config.testShardChunks.size
        val testsPerVm = config.testShardChunks.first().size
        val testsTotal = config.testShardChunks.sumBy { it.size }

        println("  Running ${runCount}x using $repeatShard VMs per run. ${runCount * repeatShard} total VMs")
        println("  $testsPerVm tests per VM. $testsTotal total tests per run")
        repeat(runCount) {
            repeat(repeatShard) { testShardsIndex ->
                jobs += async {
                    GcIosTestMatrix.build(
                            iosDevice = iosDevice,
                            testZipGcsPath = xcTestGcsPath,
                            runGcsPath = runGcsPath,
                            testShardsIndex = testShardsIndex,
                            xcTestParsed = xcTestParsed,
                            config = config).execute()
                }
            }
        }

        return afterRunTests(jobs, runGcsPath, stopwatch, config)
    }
}
