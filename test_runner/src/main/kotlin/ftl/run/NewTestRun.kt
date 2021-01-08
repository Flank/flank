package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.gc.GcStorage
import ftl.json.SavedMatrix
import ftl.json.updateMatrixMap
import ftl.json.validate
import ftl.reports.util.ReportManager
import ftl.run.common.fetchArtifacts
import ftl.run.common.pollMatrices
import ftl.run.common.saveSessionId
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.FlankTimeoutError
import ftl.run.model.TestResult
import ftl.run.platform.common.printMatricesWebLinks
import ftl.run.platform.runAndroidTests
import ftl.run.platform.runIosTests
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeoutOrNull

suspend fun IArgs.newTestRun() = withTimeoutOrNull(parsedTimeout) {
    val args: IArgs = this@newTestRun

    val (matrixMap, testShardChunks, ignoredTests) =
        cancelTestsOnTimeout(project) { runTests() }

    if (!args.async) {
        cancelTestsOnTimeout(args.project, matrixMap.map) {
            pollMatrices(matrixMap.map.keys, args).updateMatrixMap(
                matrixMap
            )
        }
        ReportManager.generate(matrixMap, args, testShardChunks, ignoredTests)
        cancelTestsOnTimeout(args.project, matrixMap.map) { fetchArtifacts(matrixMap, args) }

        matrixMap.printMatricesWebLinks(project)

        matrixMap.validate(ignoreFailedTests)
    }
}

private suspend fun IArgs.runTests(): TestResult =
    when (this) {
        is AndroidArgs -> runAndroidTests()
        is IosArgs -> runIosTests()
        else -> throw FlankGeneralError("Unknown config type")
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
