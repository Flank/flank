package ftl.reports.api

import com.google.testing.model.TestExecution
import ftl.api.PerfMetrics
import ftl.api.RemoteStorage
import ftl.api.fetchPerformanceMetrics
import ftl.api.uploadToRemoteStorage
import ftl.args.IArgs
import ftl.client.google.AndroidCatalog
import ftl.presentation.cli.firebase.test.reportmanager.ReportManagerState
import ftl.presentation.publish
import ftl.run.common.prettyPrint
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

private fun PerfMetrics.Summary.save(resultsDir: String, args: IArgs) {
    val configFilePath =
        if (args.useLocalResultDir()) Paths.get(args.localResultDir, "performanceMetrics.json")
        else Paths.get(args.localResultDir, resultsDir, "performanceMetrics.json")

    configFilePath.parent.toFile().mkdirs()

    Files.write(configFilePath, prettyPrint.toJson(this).toByteArray())
}

private fun List<Pair<TestExecution, String>>.filterAndroidPhysicalDevicesRuns() = filterNot { (testExecution, _) ->
    AndroidCatalog.isVirtualDevice(testExecution.environment.androidDevice, testExecution.projectId)
}

// TODO probably this mapping and test execution will be removed in #1756
private fun TestExecution.getPerformanceMetric(): PerfMetrics.Summary = fetchPerformanceMetrics(
    PerfMetrics.Identity(
        executionId = toolResultsStep.executionId,
        historyId = toolResultsStep.historyId,
        projectId = toolResultsStep.projectId,
        stepId = toolResultsStep.stepId
    )
)

private fun PerfMetrics.Summary.upload(
    resultBucket: String,
    resultDir: String
) = uploadPerformanceMetrics(this, resultBucket, resultDir)

internal fun uploadPerformanceMetrics(
    perfMetricsSummary: PerfMetrics.Summary,
    resultsBucket: String,
    resultDir: String
) =
    runCatching {
        uploadToRemoteStorage(
            RemoteStorage.Dir(resultsBucket, resultDir),
            RemoteStorage.Data("performanceMetrics.json", prettyPrint.toJson(perfMetricsSummary).toByteArray())
        )
    }.onFailure {
        ReportManagerState.Log("Cannot upload performance metrics ${it.message}").publish()
    }.getOrNull()
