package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.json.SavedMatrix
import ftl.reports.util.ReportManager
import ftl.run.model.TestResult
import ftl.run.common.fetchArtifacts
import ftl.run.common.pollMatrices
import ftl.run.platform.runAndroidTests
import ftl.run.platform.runIosTests
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.system.exitProcess

suspend fun newTestRun(args: IArgs) = coroutineScope {
    withTimeoutOrNull(args.parsedTimeout) {
        println(args)
        val (matrixMap, testShardChunks) = cancelTestsOnTimeout(args.project) { runTests(args) }

        if (!args.async) {
            cancelTestsOnTimeout(args.project, matrixMap.map) { pollMatrices(matrixMap, args) }
            cancelTestsOnTimeout(args.project, matrixMap.map) { fetchArtifacts(matrixMap, args) }
        }
        val exitCode = ReportManager.generate(matrixMap, args, testShardChunks)
        exitProcess(exitCode)
    }
}

private suspend fun runTests(args: IArgs): TestResult {
    return when (args) {
        is AndroidArgs -> runAndroidTests(args)
        is IosArgs -> runIosTests(args)
        else -> throw RuntimeException("Unknown config type")
    }
}

private suspend fun <T> cancelTestsOnTimeout(
    projectId: String,
    savedMatrix: Map<String, SavedMatrix>? = null,
    block: suspend () -> T
): T = coroutineScope {
    try {
        block()
    } catch (_: TimeoutCancellationException) {
        println("Canceling flank due to timeout")
        savedMatrix?.run {
            cancelMatrices(savedMatrix, projectId)
        }
        exitProcess(1)
    }
}
