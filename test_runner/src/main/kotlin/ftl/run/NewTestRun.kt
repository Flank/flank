package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.json.SavedMatrix
import ftl.json.updateMatrixMap
import ftl.reports.util.ReportManager
import ftl.run.common.fetchArtifacts
import ftl.run.common.pollMatrices
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.FlankTimeoutError
import ftl.run.model.TestResult
import ftl.run.platform.runAndroidTests
import ftl.run.platform.runIosTests
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeoutOrNull

suspend fun newTestRun(args: IArgs) = withTimeoutOrNull(args.parsedTimeout) {
    println(args)
    val (matrixMap, testShardChunks, ignoredTests) = cancelTestsOnTimeout(args.project) { runTests(args) }

    if (!args.async) {
        cancelTestsOnTimeout(args.project, matrixMap.map) { pollMatrices(matrixMap.map.keys, args).updateMatrixMap(matrixMap) }
        cancelTestsOnTimeout(args.project, matrixMap.map) { fetchArtifacts(matrixMap, args) }

        ReportManager.generate(matrixMap, args, testShardChunks, ignoredTests)
    }
}

private suspend fun runTests(args: IArgs): TestResult {
    return when (args) {
        is AndroidArgs -> runAndroidTests(args)
        is IosArgs -> runIosTests(args)
        else -> throw FlankGeneralError("Unknown config type")
    }
}

private suspend fun <T> cancelTestsOnTimeout(
    projectId: String,
    savedMatrix: Map<String, SavedMatrix>? = null,
    block: suspend () -> T
) = try {
    block()
} catch (_: TimeoutCancellationException) {
    throw FlankTimeoutError(savedMatrix, projectId)
}
