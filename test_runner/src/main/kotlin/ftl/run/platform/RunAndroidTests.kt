package ftl.run.platform

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.args.isInstrumentationTest
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.run.ANDROID_SHARD_FILE
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.TestResult
import ftl.run.platform.android.createAndroidTestConfig
import ftl.run.platform.android.createAndroidTestContexts
import ftl.run.platform.android.upload
import ftl.run.platform.android.uploadAdditionalApks
import ftl.run.platform.android.uploadOtherFiles
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.run.exception.FlankGeneralError
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.model.AndroidTestContext
import ftl.run.platform.android.asMatrixTestShards
import ftl.run.platform.android.uploadObbFiles
import ftl.run.saveShardChunks
import ftl.shard.Chunk
import ftl.shard.testCases
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun runAndroidTests(args: AndroidArgs): TestResult = coroutineScope {
    val (stopwatch, runGcsPath) = beforeRunTests(args)

    // GcAndroidMatrix => GcAndroidTestMatrix
    // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
    val devices = GcAndroidDevice.build(args.devices)
    val testMatrices = mutableListOf<Deferred<TestMatrix>>()
    val allTestShardChunks = mutableListOf<Chunk>()
    val ignoredTestsShardChunks = mutableListOf<List<String>>()

    val history = GcToolResults.createToolResultsHistory(args)
    val otherGcsFiles = args.uploadOtherFiles(runGcsPath)
    val additionalApks = args.uploadAdditionalApks(runGcsPath)
    val obbFiles = args.uploadObbFiles(runGcsPath)

    args.createAndroidTestContexts().dumpShards(args).upload(args.resultsBucket, runGcsPath)
        .forEachIndexed { contextIndex, context ->
            if (context is InstrumentationTestContext) {
                ignoredTestsShardChunks += context.ignoredTestCases
                allTestShardChunks += context.shards
            }
            val androidTestConfig = args.createAndroidTestConfig(context)
            testMatrices += executeAndroidTestMatrix(runCount = args.repeatTests) { runIndex ->
                GcAndroidTestMatrix.build(
                    androidTestConfig = androidTestConfig,
                    runGcsPath = runGcsPath.createGcsPath(contextIndex, runIndex),
                    additionalApkGcsPaths = additionalApks,
                    androidDeviceList = devices,
                    args = args,
                    otherFiles = otherGcsFiles,
                    toolResultsHistory = history,
                    obbFiles = obbFiles
                )
            }
        }

    if (testMatrices.isEmpty()) throw FlankGeneralError("There are no tests to run.")

    println(beforeRunMessage(args, allTestShardChunks))

    TestResult(
        matrixMap = afterRunTests(testMatrices.awaitAll(), runGcsPath, stopwatch, args),
        shardChunks = allTestShardChunks.testCases,
        ignoredTests = ignoredTestsShardChunks.flatten()
    )
}

private fun String.createGcsPath(contextIndex: Int, runIndex: Int) =
    if (runIndex == 0) "$this/matrix_$contextIndex/"
    else "$this/matrix_${contextIndex}_$runIndex/"

private fun List<AndroidTestContext>.dumpShards(config: AndroidArgs) = takeIf { config.isInstrumentationTest }?.apply {
    filterIsInstance<InstrumentationTestContext>().asMatrixTestShards().saveShards(config.obfuscateDumpShards)
    if (config.disableResultsUpload.not()) GcStorage.upload(ANDROID_SHARD_FILE, config.resultsBucket, config.resultsDir)
} ?: this

private fun AndroidMatrixTestShards.saveShards(obfuscateOutput: Boolean) = saveShardChunks(
    shardFilePath = ANDROID_SHARD_FILE,
    shards = this,
    size = size,
    obfuscatedOutput = obfuscateOutput
)

private suspend fun executeAndroidTestMatrix(
    runCount: Int,
    createTestMatrix: (runIndex: Int) -> Testing.Projects.TestMatrices.Create
): List<Deferred<TestMatrix>> = coroutineScope {
    (0 until runCount).map {
        async(Dispatchers.IO) {
            createTestMatrix(it).executeWithRetry()
        }
    }
}
