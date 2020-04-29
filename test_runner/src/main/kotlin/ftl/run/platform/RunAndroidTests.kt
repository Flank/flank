package ftl.run.platform

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.args.AndroidTestShard
import ftl.args.ShardChunks
import ftl.args.yml.ResolvedApks
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.run.model.TestResult
import ftl.run.platform.android.createAndroidTestConfig
import ftl.run.platform.android.resolveApks
import ftl.run.platform.android.uploadApks
import ftl.run.platform.android.uploadOtherFiles
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

    val testMatrices = mutableListOf<Deferred<TestMatrix>>()
    val allTestShardChunks = mutableListOf<List<String>>()

    val history = GcToolResults.createToolResultsHistory(args)
    val otherGcsFiles = args.uploadOtherFiles(runGcsPath)

    args.resolveApks().forEach { apks: ResolvedApks ->
        val testShards = apks.test?.let { test ->
            AndroidTestShard.getTestShardChunks(args, test)
        }
        // We can't return if testShards is null since it can be a robo test.
        testShards?.let {
            val shardsWithAtLeastOneTest = testShards.filterAtLeastOneTest()
            if (shardsWithAtLeastOneTest.isEmpty()) {
                // No tests to run, skipping the execution.
                return@forEach
            }
            allTestShardChunks += shardsWithAtLeastOneTest
        }

        val uploadedApks = uploadApks(
            apks = apks,
            args = args,
            runGcsPath = runGcsPath
        )

        val androidTestConfig = args.createAndroidTestConfig(
            uploadedApks = uploadedApks,
            testShards = testShards,
            runGcsPath = runGcsPath
        )

        testMatrices += executeAndroidTestMatrix(runCount = args.repeatTests) {
            GcAndroidTestMatrix.build(
                androidTestConfig = androidTestConfig,
                runGcsPath = runGcsPath,
                additionalApkGcsPaths = uploadedApks.additionalApks,
                androidDeviceList = androidDeviceList,
                args = args,
                otherFiles = otherGcsFiles,
                toolResultsHistory = history
            )
        }
    }
    println(beforeRunMessage(args, allTestShardChunks))
    val matrixMap = afterRunTests(testMatrices.awaitAll(), runGcsPath, stopwatch, args)
    matrixMap to allTestShardChunks
}

private suspend fun executeAndroidTestMatrix(
    runCount: Int,
    createTestMatrix: () -> Testing.Projects.TestMatrices.Create
): List<Deferred<TestMatrix>> = coroutineScope {
    (0 until runCount).map {
        async(Dispatchers.IO) {
            createTestMatrix().executeWithRetry()
        }
    }
}

private fun ShardChunks.filterAtLeastOneTest(): ShardChunks = filter { chunk -> chunk.isNotEmpty() }
