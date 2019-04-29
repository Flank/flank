package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.args.AndroidTestShard
import ftl.args.yml.AppTestPair
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.json.MatrixMap
import ftl.util.ShardCounter
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object AndroidTestRunner {

    suspend fun runTests(androidArgs: AndroidArgs): Pair<MatrixMap, List<List<String>>> = coroutineScope {
        val (stopwatch, runGcsPath) = GenericTestRunner.beforeRunTests(androidArgs)

        // GcAndroidMatrix => GcAndroidTestMatrix
        // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
        val androidDeviceList = GcAndroidDevice.build(androidArgs.devices)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = androidArgs.repeatTests
        val shardCounter = ShardCounter()
        val history = GcToolResults.createToolResultsHistory(androidArgs)
        val apks = resolveApks(androidArgs, runGcsPath)
        val allTestShardChunks: MutableList<List<String>> = mutableListOf()

        apks.forEach { apk ->
            // ensure we only shard tests that are part of the test apk
            val testShardChunks = AndroidTestShard.getTestShardChunks(androidArgs, apk.test)
            allTestShardChunks += testShardChunks
            repeat(runCount) {
                testShardChunks.forEach { testTargets ->
                    jobs += async {
                        GcAndroidTestMatrix.build(
                            appApkGcsPath = apk.app ?: androidArgs.appApk,
                            testApkGcsPath = apk.test,
                            runGcsPath = runGcsPath,
                            androidDeviceList = androidDeviceList,
                            testTargets = testTargets,
                            args = androidArgs,
                            shardCounter = shardCounter,
                            toolResultsHistory = history
                        ).executeWithRetry()
                    }
                }
            }
        }

        println(GenericTestRunner.beforeRunMessage(androidArgs, allTestShardChunks))
        val matrixMap = GenericTestRunner.afterRunTests(jobs.awaitAll(), runGcsPath, stopwatch, androidArgs)
        matrixMap to allTestShardChunks
    }

    /**
     * Upload APKs if the path given is local
     *
     * @return Pair(gcs uri for app apk, gcs uri for test apk)
     */
    private suspend fun resolveApks(args: AndroidArgs, runGcsPath: String): List<AppTestPair> = coroutineScope {
        val gcsBucket = args.resultsBucket
        val appTestApks = listOf(AppTestPair(app = args.appApk, test = args.testApk)) + args.additionalAppTestApks
        val result = mutableListOf<AppTestPair>()

        appTestApks.forEach { apks ->
            val appApkGcsPath = async { GcStorage.upload(apks.app ?: args.appApk, gcsBucket, runGcsPath) }
            val testApkGcsPath = async { GcStorage.upload(apks.test, gcsBucket, runGcsPath) }

            result.add(
                AppTestPair(
                    app = appApkGcsPath.await(),
                    test = testApkGcsPath.await()
                )
            )
        }

        result
    }
}
