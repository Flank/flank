package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.config.AndroidConfig
import ftl.config.FtlConstants
import ftl.config.YamlConfig
import ftl.gc.GcAndroidMatrix
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.json.MatrixMap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

object AndroidTestRunner : GenericTestRunner<AndroidConfig> {

    override suspend fun runTests(yamlConfig: YamlConfig<AndroidConfig>): MatrixMap {
        val (stopwatch, runGcsPath) = beforeRunTests()

        // GcAndroidMatrix => GcAndroidTestMatrix
        // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
        val androidDeviceList = GcAndroidMatrix.build(yamlConfig.gCloudConfig.devices)

        val apks = resolveApks(yamlConfig.gCloudConfig, runGcsPath)
        println("apks: $apks")

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
                    GcAndroidTestMatrix.build(
                            appApkGcsPath = apks.first,
                            testApkGcsPath = apks.second,
                            runGcsPath = runGcsPath,
                            androidDeviceList = androidDeviceList,
                            testShardsIndex = testShardsIndex,
                            config = yamlConfig).execute()
                }
            }
        }

        return afterRunTests(jobs, runGcsPath, stopwatch, yamlConfig)
    }

    /**
     * Upload APKs if the path given is local
     *
     * @return Pair(gcs uri for app apk, gcs uri for test apk)
     */
    private suspend fun resolveApks(config: AndroidConfig, runGcsPath: String): Pair<String, String> {
        val gcsBucket = config.getGcsBucket()

        val appApkGcsPath = async {
            if (!config.appApk.startsWith(FtlConstants.GCS_PREFIX)) {
                GcStorage.uploadAppApk(config, gcsBucket, runGcsPath)
            } else {
                config.appApk
            }
        }

        val testApkGcsPath = async {
            // Skip download case for testApk - YamlConfig downloads it when it does validation
            if (!config.testApk.startsWith(FtlConstants.GCS_PREFIX)) {
                GcStorage.uploadTestApk(config, gcsBucket, runGcsPath)
            } else {
                config.testApk
            }
        }

        return Pair(appApkGcsPath.await(), testApkGcsPath.await())
    }
}
