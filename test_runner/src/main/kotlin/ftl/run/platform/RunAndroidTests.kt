package ftl.run.platform

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.args.shouldSplitRuns
import ftl.args.splitConfigurationByDeviceType
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
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
import ftl.util.FlankCommonException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

internal suspend fun runAndroidTests(args: AndroidArgs): TestResult = coroutineScope {
    val (stopwatch, runGcsPath) = beforeRunTests(args)

    // GcAndroidMatrix => GcAndroidTestMatrix
    // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
    val testMatrices = mutableListOf<Deferred<TestMatrix>>()
    val allTestShardChunks = mutableListOf<List<String>>()
    val ignoredTestsShardChunks = mutableListOf<List<String>>()

    val history = GcToolResults.createToolResultsHistory(args)
    val otherGcsFiles = args.uploadOtherFiles(runGcsPath)
    val additionalApks = args.uploadAdditionalApks(runGcsPath)
    val argsList = if (args.shouldSplitRuns()) args.splitConfigurationByDeviceType() else listOf(args)

    argsList.forEach {
        it.createAndroidTestContexts()
            .upload(it.resultsBucket, runGcsPath)
            .forEachIndexed { index, context ->
                if (context is InstrumentationTestContext) {
                    ignoredTestsShardChunks += context.ignoredTestCases
                    allTestShardChunks += context.shards
                }
                val androidTestConfig = it.createAndroidTestConfig(context)
                testMatrices += executeAndroidTestMatrix(runCount = it.repeatTests) {
                    GcAndroidTestMatrix.build(
                        androidTestConfig = androidTestConfig,
                        runGcsPath = "$runGcsPath/matrix_$index/",
                        additionalApkGcsPaths = additionalApks,
                        androidDeviceList = GcAndroidDevice.build(it.devices),
                        args = it,
                        otherFiles = otherGcsFiles,
                        toolResultsHistory = history
                    )
                }
            }
    }

    if (testMatrices.isEmpty()) throw FlankCommonException("There are no tests to run.")

    println(beforeRunMessage(args, allTestShardChunks))
    TestResult(
        matrixMap = afterRunTests(testMatrices.awaitAll(), runGcsPath, stopwatch, args),
        shardChunks = allTestShardChunks,
        ignoredTests = ignoredTestsShardChunks.flatten()
    )
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
