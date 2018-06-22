package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.config.YamlConfig
import ftl.gc.GcAndroidMatrix
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object AndroidTestRunner : GenericTestRunner {

    /** @return Pair(app apk, test apk) **/
    private suspend fun uploadApksInParallel(config: YamlConfig, runGcsPath: String): Pair<String, String> {
        val gcsBucket = config.getGcsBucket()
        val appApkGcsPath = async { GcStorage.uploadAppApk(config, gcsBucket, runGcsPath) }
        val testApkGcsPath = async { GcStorage.uploadTestApk(config, gcsBucket, runGcsPath) }

        return Pair(appApkGcsPath.await(), testApkGcsPath.await())
    }

    override suspend fun runTests(config: YamlConfig): MatrixMap {
        val (stopwatch, runGcsPath) = beforeRunTests()

        // GcAndroidMatrix => GcAndroidTestMatrix
        // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
        val androidDeviceList = GcAndroidMatrix.build(config.devices)

        val apks = uploadApksInParallel(config, runGcsPath)

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
                    GcAndroidTestMatrix.build(
                            appApkGcsPath = apks.first,
                            testApkGcsPath = apks.second,
                            runGcsPath = runGcsPath,
                            androidDeviceList = androidDeviceList,
                            testShardsIndex = testShardsIndex,
                            config = config).execute()
                }
            }
        }

        return afterRunTests(jobs, runGcsPath, stopwatch, config)
    }
}
