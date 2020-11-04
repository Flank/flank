package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.toolresults.model.PerfMetricsSummary
import ftl.android.AndroidCatalog
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

internal fun List<Pair<TestExecution, String>>.getAndUploadPerformanceMetrics(
    resultBucket: String
) = runBlocking {
    filterAndroidPhysicalDevicesRuns()
        .map { (testExecution, gcsStoragePath) ->
            async(Dispatchers.IO) {
                testExecution.getPerformanceMetric().upload(resultBucket = resultBucket, resultDir = gcsStoragePath)
            }
        }
        .awaitAll()
}

private fun List<Pair<TestExecution, String>>.filterAndroidPhysicalDevicesRuns() = filterNot { (testExecution, _) ->
    AndroidCatalog.isVirtualDevice(testExecution.environment.androidDevice, testExecution.projectId)
}

private fun TestExecution.getPerformanceMetric() = GcToolResults.getPerformanceMetric(toolResultsStep)

private fun PerfMetricsSummary.upload(
    resultBucket: String,
    resultDir: String
) = GcStorage.uploadPerformanceMetrics(this, resultBucket, resultDir)
