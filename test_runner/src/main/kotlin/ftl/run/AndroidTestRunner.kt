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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

object AndroidTestRunner {

    suspend fun runTests(args: AndroidArgs): Pair<MatrixMap, List<List<String>>> = coroutineScope {
        val (stopwatch, runGcsPath) = GenericTestRunner.beforeRunTests(args)

        // GcAndroidMatrix => GcAndroidTestMatrix
        // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
        val androidDeviceList = GcAndroidDevice.build(args.devices)

        val jobs = arrayListOf<Deferred<TestMatrix>>()
        val runCount = args.repeatTests
        val shardCounter = ShardCounter()
        val history = GcToolResults.createToolResultsHistory(args)
        val appTestApks = listOf(AppTestPair(app = args.appApk, test = args.testApk)) + args.additionalAppTestApks
        val allTestShardChunks: List<List<String>> = appTestApks.map { localApk ->
            val apk = resolveApk(localApk, args, runGcsPath)
            // ensure we only shard tests that are part of the test apk
            val testShards = AndroidTestShard.getTestShardChunks(args, localApk.test)
            repeat(runCount) {
                testShards.forEach { testTargets ->
                    // specify dispatcher to avoid inheriting main runBlocking context that runs in the main thread
                    // https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html
                    jobs += async(Dispatchers.IO) {
                        GcAndroidTestMatrix.build(
                            appApkGcsPath = apk.app,
                            testApkGcsPath = apk.test,
                            runGcsPath = runGcsPath,
                            androidDeviceList = androidDeviceList,
                            testTargets = testTargets,
                            args = args,
                            shardCounter = shardCounter,
                            toolResultsHistory = history
                        ).executeWithRetry()
                    }
                }
            }
            testShards
        }.flatten()

        println(GenericTestRunner.beforeRunMessage(args, allTestShardChunks))
        val matrixMap = GenericTestRunner.afterRunTests(jobs.awaitAll(), runGcsPath, stopwatch, args)
        matrixMap to allTestShardChunks
    }

    /**
     * Upload an APK pair if the path given is local
     *
     * @return AppTestPair with their GCS paths
     */
    private suspend fun resolveApk(
        apk: AppTestPair,
        args: AndroidArgs,
        runGcsPath: String
    ): AppTestPair = coroutineScope {
        val gcsBucket = args.resultsBucket

        val appApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apk.app, gcsBucket, runGcsPath) }
        val testApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apk.test, gcsBucket, runGcsPath) }

        AppTestPair(
            app = appApkGcsPath.await(),
            test = testApkGcsPath.await()
        )
    }
}
