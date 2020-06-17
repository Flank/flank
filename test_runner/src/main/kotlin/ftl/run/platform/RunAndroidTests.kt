package ftl.run.platform

import com.google.api.services.testing.Testing
import com.google.api.services.testing.model.TestMatrix
import ftl.args.AndroidArgs
import ftl.gc.GcAndroidDevice
import ftl.gc.GcAndroidTestMatrix
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.model.TestResult
import ftl.run.platform.android.*
import ftl.run.platform.android.createAndroidTestConfig
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
import java.util.concurrent.atomic.AtomicInteger

internal suspend fun runAndroidTests(args: AndroidArgs): TestResult = coroutineScope {
    val (stopwatch, runGcsPath) = beforeRunTests(args)

    // GcAndroidMatrix => GcAndroidTestMatrix
    // GcAndroidTestMatrix.execute() 3x retry => matrix id (string)
    val androidDeviceList = GcAndroidDevice.build(args.devices)

    val testMatrices = mutableListOf<Deferred<TestMatrix>>()
    val allTestShardChunks = mutableListOf<List<String>>()

    val history = GcToolResults.createToolResultsHistory(args)
    val otherGcsFiles = args.uploadOtherFiles(runGcsPath)
    val additionalApks = args.uploadAdditionalApks(runGcsPath)

    args.createAndroidTestContexts()
        .upload(args.resultsBucket, runGcsPath)
        .forEach { context ->
            if (context is InstrumentationTestContext) allTestShardChunks += context.shards
            val androidTestConfig = args.createAndroidTestConfig(context)
            testMatrices += executeAndroidTestMatrix(runCount = args.repeatTests) {
                GcAndroidTestMatrix.build(
                    androidTestConfig = androidTestConfig,
                    runGcsPath = "$runGcsPath/matrix_${context.index}/",
                    additionalApkGcsPaths = additionalApks,
                    androidDeviceList = androidDeviceList,
                    args = args,
                    otherFiles = otherGcsFiles,
                    toolResultsHistory = history
                )
            }
        }

    if (testMatrices.isEmpty()) throw FlankCommonException("There are no tests to run.")

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

private val regex = ".*_(\\d)\\.apk".toRegex()

private val AndroidTestContext.index
    get() = when (this) {
        is InstrumentationTestContext -> getAppendedNumber(test.gcs) ?: testApkCounter.getAndIncrement()
        is RoboTestContext -> "robo_${roboCounter.getAndIncrement()}"
    }
private inline fun getAppendedNumber(gcsPath: String) = regex.find(gcsPath)?.let { it.groups[1]?.value }
private val roboCounter = AtomicInteger(0)
