package ftl.run.platform

import com.google.testing.model.TestMatrix
import ftl.args.IosArgs
import ftl.gc.GcIosMatrix
import ftl.gc.GcIosTestMatrix
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.http.executeWithRetry
import ftl.ios.xctest.common.mapToRegex
import ftl.log.logLn
import ftl.ios.xctest.flattenShardChunks
import ftl.ios.xctest.xcTestRunFlow
import ftl.run.IOS_SHARD_FILE
import ftl.run.dumpShards
import ftl.run.exception.FlankGeneralError
import ftl.run.model.TestResult
import ftl.run.platform.android.uploadAdditionalIpas
import ftl.run.platform.android.uploadOtherFiles
import ftl.run.platform.common.afterRunTests
import ftl.run.platform.common.beforeRunMessage
import ftl.run.platform.common.beforeRunTests
import ftl.shard.testCases
import ftl.util.ShardCounter
import ftl.util.asFileReference
import ftl.util.repeat
import ftl.util.uploadIfNeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

// https://github.com/bootstraponline/gcloud_cli/blob/5bcba57e825fc98e690281cf69484b7ba4eb668a/google-cloud-sdk/lib/googlecloudsdk/api_lib/firebase/test/ios/matrix_creator.py#L109
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
// https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/
internal suspend fun IosArgs.runIosTests(): TestResult =
    coroutineScope {
        val args = this@runIosTests
        val stopwatch = beforeRunTests()

        val iosDeviceList = GcIosMatrix.build(devices)

        val shardCounter = ShardCounter()
        val history = GcToolResults.createToolResultsHistory(args)
        val otherGcsFiles = uploadOtherFiles()
        val additionalIpasGcsFiles = uploadAdditionalIpas()

        dumpShards()
        if (disableResultsUpload.not())
            GcStorage.upload(IOS_SHARD_FILE, resultsBucket, resultsDir)

        args.validateXcTestRunData()

        // Upload only after parsing shards to detect missing methods early.
        val xcTestGcsPath = uploadIfNeeded(xctestrunZip.asFileReference()).gcs
        val testShardChunks = xcTestRunData.flattenShardChunks()

        logLn(beforeRunMessage(testShardChunks))

        val result: List<TestMatrix> = xcTestRunFlow()
            .map { xcTestRun ->
                GcIosTestMatrix.build(
                    iosDeviceList = iosDeviceList,
                    testZipGcsPath = xcTestGcsPath,
                    xcTestRun = xcTestRun,
                    args = args,
                    shardCounter = shardCounter,
                    toolResultsHistory = history,
                    otherFiles = otherGcsFiles,
                    additionalIpasGcsPaths = additionalIpasGcsFiles
                )
            }
            .repeat(repeatTests)
            .map { async(Dispatchers.IO) { it.executeWithRetry() } }
            .toList()
            .awaitAll()

        TestResult(
            matrixMap = afterRunTests(
                testMatrices = result,
                stopwatch = stopwatch
            ),
            shardChunks = testShardChunks.testCases
        )
    }

private fun IosArgs.validateXcTestRunData() {
    if (!disableSharding && testTargets.isNotEmpty()) {
        val filteredMethods = xcTestRunData
            .shardTargets.values
            .flatten()
            .flatMap { it.values }
            .flatten()

        if (filteredMethods.isEmpty()) throw FlankGeneralError(
            "Empty shards. Cannot match any method to $testTargets"
        )

        if (filteredMethods.size < testTargets.size) {
            val regexList = testTargets.mapToRegex()

            val notMatched = testTargets.filter {
                filteredMethods.all { method ->
                    regexList.any { regex ->
                        regex.matches(method)
                    }
                }
            }

            logLn("WARNING: cannot match test_targets: $notMatched")
        }
    }
}
