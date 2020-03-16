package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.util.ReportManager
import ftl.run.model.TestResult
import ftl.run.common.fetchArtifacts
import ftl.run.common.pollMatrices
import ftl.run.platform.runAndroidTests
import ftl.run.platform.runIosTests
import kotlinx.coroutines.coroutineScope
import kotlin.system.exitProcess

suspend fun newTestRun(args: IArgs) = coroutineScope {
    println(args)
    val (matrixMap, testShardChunks) = runTests(args)

    if (!args.async) {
        pollMatrices(matrixMap, args)
        fetchArtifacts(matrixMap, args)

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
