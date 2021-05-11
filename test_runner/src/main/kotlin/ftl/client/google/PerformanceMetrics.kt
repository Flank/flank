package ftl.client.google

import com.google.testing.model.TestExecution
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.json.MatrixMap
import ftl.reports.api.getAndUploadPerformanceMetrics

fun List<TestExecution>.createAndUploadPerformanceMetricsForAndroid(
    args: IArgs,
    matrices: MatrixMap
) = also {
    takeIf { args is AndroidArgs }
        ?.run { withGcsStoragePath(matrices, args.resultsDir).getAndUploadPerformanceMetrics(args) }
}

private fun List<TestExecution>.withGcsStoragePath(
    matrices: MatrixMap,
    defaultResultDir: String
) = map { testExecution ->
    testExecution to (matrices.map[testExecution.matrixId]?.gcsPathWithoutRootBucket ?: defaultResultDir)
}
