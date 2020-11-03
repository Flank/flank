package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.toolresults.model.PerfMetricsSummary
import ftl.android.AndroidCatalog
import ftl.args.IArgs
import ftl.gc.GcStorage
import ftl.gc.GcToolResults
import ftl.json.MatrixMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

internal fun List<TestExecution>.createAndUploadPerformanceMetrics(
    args: IArgs,
    matrices: MatrixMap
) = runBlocking {
    filterAndroidPhysicalDevicesRuns()
        .map { testExecution ->
            async(Dispatchers.IO) {
                testExecution.createPerformanceMetric().upload(
                    resultBucket = args.resultsBucket,
                    resultDir = matrices.map[testExecution.matrixId]?.gcsPathWithoutRootBucket ?: args.resultsDir
                )
            }
        }
        .awaitAll()
}

private fun List<TestExecution>.filterAndroidPhysicalDevicesRuns() = filterNot {
    AndroidCatalog.isVirtualDevice(it.environment.androidDevice, it.projectId)
}

private fun TestExecution.createPerformanceMetric() = GcToolResults.createPerformanceMetric(toolResultsStep)

private fun PerfMetricsSummary.upload(
    resultBucket: String,
    resultDir: String
) = GcStorage.uploadPerformanceMetrics(this, resultBucket, resultDir)
