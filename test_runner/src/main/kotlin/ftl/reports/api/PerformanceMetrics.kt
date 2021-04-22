package ftl.reports.api

import com.google.api.services.toolresults.model.PerfMetricsSummary
import com.google.testing.model.TestExecution
import flank.common.logLn
import ftl.api.RemoteStorage
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.client.google.AndroidCatalog
import ftl.gc.GcToolResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths

internal fun List<Pair<TestExecution, String>>.getAndUploadPerformanceMetrics(
    args: IArgs
) = runBlocking {
    filterAndroidPhysicalDevicesRuns()
        .map { (testExecution, gcsStoragePath) ->
            async(Dispatchers.IO) {
                val performanceMetrics = testExecution.getPerformanceMetric()
                performanceMetrics.save(gcsStoragePath, args)
                if (args.disableResultsUpload.not()) {
                    performanceMetrics.upload(resultBucket = args.resultsBucket, resultDir = gcsStoragePath)
                }
            }
        }
        .awaitAll()
}

private fun PerfMetricsSummary.save(resultsDir: String, args: IArgs) {
    val configFilePath =
        if (args.useLocalResultDir()) Paths.get(args.localResultDir, "performanceMetrics.json")
        else Paths.get(args.localResultDir, resultsDir, "performanceMetrics.json")

    configFilePath.parent.toFile().mkdirs()
    Files.write(configFilePath, toPrettyString().toByteArray())
}

private fun List<Pair<TestExecution, String>>.filterAndroidPhysicalDevicesRuns() = filterNot { (testExecution, _) ->
    AndroidCatalog.isVirtualDevice(testExecution.environment.androidDevice, testExecution.projectId)
}

private fun TestExecution.getPerformanceMetric() = GcToolResults.getPerformanceMetric(toolResultsStep)

private fun PerfMetricsSummary.upload(
    resultBucket: String,
    resultDir: String
) = uploadPerformanceMetrics(this, resultBucket, resultDir)

internal fun uploadPerformanceMetrics(perfMetricsSummary: PerfMetricsSummary, resultsBucket: String, resultDir: String) =
    runCatching {
        uploadToRemoteStorage(
            RemoteStorage.Dir(resultsBucket, resultDir),
            RemoteStorage.Data("performanceMetrics.json", perfMetricsSummary.toPrettyString().toByteArray())
        )
    }.onFailure {
        logLn("Cannot upload performance metrics ${it.message}")
    }.getOrNull()
