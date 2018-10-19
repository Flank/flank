package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.config.FtlConstants
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.json.MatrixMap
import ftl.util.ShardCounter
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async

object AndroidTestRunner {

    suspend fun runTests(androidArgs: AndroidArgs): MatrixMap {
        val (stopwatch, runGcsPath) = GenericTestRunner.beforeRunTests()

        // GcAndroidMatrix => GcAndroidTestMatrix
        // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
        val androidDeviceList = GcAndroidDevice.build(androidArgs.devices)

        val apks = resolveApks(androidArgs, runGcsPath)
        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = androidArgs.repeatTests
        val deviceCount = androidArgs.testShardChunks.size
        val shardCounter = ShardCounter()
        val history = GcToolResults.createToolResultsHistory(androidArgs)

        println(GenericTestRunner.beforeRunMessage(androidArgs))
        repeat(runCount) {
            repeat(deviceCount) { testShardsIndex ->
                jobs += async {
                    GcAndroidTestMatrix.build(
                        appApkGcsPath = apks.first,
                        testApkGcsPath = apks.second,
                        runGcsPath = runGcsPath,
                        androidDeviceList = androidDeviceList,
                        testShardsIndex = testShardsIndex,
                        config = androidArgs,
                        shardCounter = shardCounter,
                        toolResultsHistory = history
                    ).execute()
                }
            }
        }

        return GenericTestRunner.afterRunTests(jobs, runGcsPath, stopwatch, androidArgs)
    }

    /**
     * Upload APKs if the path given is local
     *
     * @return Pair(gcs uri for app apk, gcs uri for test apk)
     */
    private suspend fun resolveApks(config: AndroidArgs, runGcsPath: String): Pair<String, String> {
        val gcsBucket = config.resultsBucket

        val appApkGcsPath = GlobalScope.async {
            if (!config.appApk.startsWith(FtlConstants.GCS_PREFIX)) {
                GcStorage.uploadAppApk(config, gcsBucket, runGcsPath)
            } else {
                config.appApk
            }
        }

        val testApkGcsPath = GlobalScope.async {
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
