package ftl.run.platform

import com.google.api.services.testing.model.AndroidDeviceList
import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.ToolResultsHistory
import ftl.args.AndroidArgs
import ftl.args.AndroidTestShard
import ftl.args.ShardChunks
import ftl.args.yml.AppTestPair
import ftl.args.yml.ResolvedTestApks
import ftl.args.yml.UploadedTestApks
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
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

    val testMatrices = arrayListOf<Deferred<TestMatrix>>()
    val runCount = args.repeatTests
    val history = GcToolResults.createToolResultsHistory(args)
    val resolvedTestApks = listOf(
        element = ResolvedTestApks(
            app = args.appApk,
            test = args.testApk,
            additionalTests = args.additionalTestApks
        )
    ).plus(
        elements = args.additionalAppTestApks
            .provideMissingApps(args.appApk)
    )

    val allTestShardChunks: ShardChunks = resolvedTestApks.map { apks: ResolvedTestApks ->
        // Ensure we only shard tests that are part of the test apk. Use the resolved test apk path to make sure
        // we don't re-download an apk it is on the local file system.
        AndroidTestShard.getTestShardChunks(args, apks).also { testShards ->
            testMatrices += executeAndroidTestMatrix(
                uploadedTestApks = uploadTestApks(
                    apks = apks,
                    args = args,
                    runGcsPath = runGcsPath
                ),
                runGcsPath = runGcsPath,
                androidDeviceList = androidDeviceList,
                testShards = testShards,
                args = args,
                history = history,
                runCount = runCount
            )
        }
    }.flatten()

    println(beforeRunMessage(args, allTestShardChunks))
    val matrixMap = afterRunTests(testMatrices.awaitAll(), runGcsPath, stopwatch, args)
    matrixMap to allTestShardChunks
}

private fun List<AppTestPair>.provideMissingApps(withFallbackApp: String) =
    map { ResolvedTestApks(app = it.app ?: withFallbackApp, test = it.test) }

private suspend fun executeAndroidTestMatrix(
    runGcsPath: String,
    args: AndroidArgs,
    testShards: ShardChunks,
    uploadedTestApks: UploadedTestApks,
    androidDeviceList: AndroidDeviceList,
    history: ToolResultsHistory,
    runCount: Int
): List<Deferred<TestMatrix>> = coroutineScope {
    (0 until runCount).map {
        async(Dispatchers.IO) {
            GcAndroidTestMatrix.build(
                appApkGcsPath = uploadedTestApks.app,
                testApkGcsPath = uploadedTestApks.test,
                runGcsPath = runGcsPath,
                androidDeviceList = androidDeviceList,
                testShards = testShards,
                args = args,
                toolResultsHistory = history,
                additionalTestGcsPaths = uploadedTestApks.additionalTests
            ).executeWithRetry()
        }
    }
}

/**
 * Upload an APK pair if the path given is local
 *
 * @return AppTestPair with their GCS paths
 */
private suspend fun uploadTestApks(
    apks: ResolvedTestApks,
    args: AndroidArgs,
    runGcsPath: String
): UploadedTestApks = coroutineScope {
    val gcsBucket = args.resultsBucket

    val appApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apks.app, gcsBucket, runGcsPath) }
    val testApkGcsPath = async(Dispatchers.IO) { GcStorage.upload(apks.test, gcsBucket, runGcsPath) }
    val additionalTestApkGcsPaths = apks.additionalTests.map { async(Dispatchers.IO) { GcStorage.upload(it, gcsBucket, runGcsPath) } }

    UploadedTestApks(
        app = appApkGcsPath.await(),
        test = testApkGcsPath.await(),
        additionalTests = additionalTestApkGcsPaths.awaitAll()
    )
}
