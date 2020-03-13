package ftl.run.platform

import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.args.AndroidTestShard
import ftl.args.yml.AppTestPair
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.args.ShardChunks
import ftl.args.yml.ResolvedTestPair
import ftl.args.yml.UploadedTestPair
import ftl.run.model.TestResult
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun runAndroidTests(args: AndroidArgs): TestResult = coroutineScope {
    val (stopwatch, runGcsPath) = beforeRunTests(args)

    // GcAndroidMatrix => GcAndroidTestMatrix
    // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
    val androidDeviceList = GcAndroidDevice.build(args.devices)

    val jobs = arrayListOf<Deferred<TestMatrix>>()
    val runCount = args.repeatTests
    val history = GcToolResults.createToolResultsHistory(args)
    val apkPairsInArgs = listOf(AppTestPair(app = args.appApk, test = args.testApk)) + args.additionalAppTestApks
    val allTestShardChunks: ShardChunks = apkPairsInArgs
        .provideMissingApps(withFallbackApp = args.appApk)
        .map { resolvedApkPair ->
            val uploadedApkPair = resolveApkPair(resolvedApkPair, args, runGcsPath)
            // Ensure we only shard tests that are part of the test apk. Use the resolved test apk path to make sure
            // we don't re-download an apk it is on the local file system.
            AndroidTestShard.getTestShardChunks(args, resolvedApkPair.test).also { testShards ->
                repeat(runCount) {
                    // specify dispatcher to avoid inheriting main runBlocking context that runs in the main thread
                    // https://kotlinlang.org/docs/reference/coroutines/coroutine-context-and-dispatchers.html
                    jobs += async(Dispatchers.IO) {
                        GcAndroidTestMatrix.build(
                            appApkGcsPath = uploadedApkPair.app,
                            testApkGcsPath = uploadedApkPair.test,
                            runGcsPath = runGcsPath,
                            androidDeviceList = androidDeviceList,
                            testShards = testShards,
                            args = args,
                            toolResultsHistory = history
                        ).executeWithRetry()
                    }
                }
            }
        }.flatten()

    println(beforeRunMessage(args, allTestShardChunks))
    val matrixMap = afterRunTests(jobs.awaitAll(), runGcsPath, stopwatch, args)
    matrixMap to allTestShardChunks
}

private fun List<AppTestPair>.provideMissingApps(withFallbackApp: String) =
    map { ResolvedTestPair(app = it.app ?: withFallbackApp, test = it.test) }

/**
 * Upload an APK pair if the path given is local
 *
 * @return AppTestPair with their GCS paths
 */
private suspend fun resolveApkPair(
    apk: ResolvedTestPair,
    args: AndroidArgs,
    runGcsPath: String
): UploadedTestPair = coroutineScope {
    val gcsBucket = args.resultsBucket

    val appApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apk.app, gcsBucket, runGcsPath) }
    val testApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apk.test, gcsBucket, runGcsPath) }

    UploadedTestPair(
        app = appApkGcsPath.await(),
        test = testApkGcsPath.await()
    )
}
