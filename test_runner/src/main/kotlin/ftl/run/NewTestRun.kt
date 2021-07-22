package ftl.run

import flank.tool.analytics.mixpanel.add
import flank.tool.analytics.mixpanel.analyticsReport
import flank.tool.analytics.mixpanel.send
import ftl.api.TestMatrix
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.json.MatrixMap
import ftl.json.updateMatrixMap
import ftl.json.validate
import ftl.reports.addStepTime
import ftl.reports.output.log
import ftl.reports.output.outputReport
import ftl.reports.util.ReportManager
import ftl.run.common.fetchAllTestRunArtifacts
import ftl.run.common.pollMatrices
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.FlankTimeoutError
import ftl.run.model.TestResult
import ftl.run.platform.common.printMatricesWebLinks
import ftl.run.platform.runAndroidTests
import ftl.run.platform.runIosTests
import ftl.util.measureTime
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

        val duration = measureTime {
            cancelTestsOnTimeout(args.project, matrixMap.map) { fetchAllTestRunArtifacts(matrixMap, args) }
            ReportManager.generate(matrixMap, args, testShardChunks, ignoredTests)
        }

        matrixMap.printMatricesWebLinks(project)
        outputReport.log(matrixMap)
        matrixMap.reportTestResults()
        analyticsReport.send()
        matrixMap.validate(ignoreFailedTests)
        addStepTime("Generating reports", duration)
    }
}

private fun MatrixMap.reportTestResults() {
    val outcomes = map.flatMap { it.value.axes }
    val testsSummary = outcomes.map { it.suiteOverview }.fold(TestMatrix.SuiteOverview()) { result, overview ->
        result.copy(
            total = result.total + overview.total,
            errors = result.errors + overview.errors,
            failures = result.failures + overview.failures,
            flakes = result.flakes + overview.flakes,
            skipped = result.skipped + overview.skipped,
            elapsedTime = result.elapsedTime + overview.elapsedTime,
            overheadTime = result.overheadTime + overview.overheadTime
        )
    }
    analyticsReport.add(
        "shards_count",
        map.values.flatMap { it.testExecutions }.maxOf { testExecution -> testExecution.shardIndex ?: 0 } + 1
    )
    analyticsReport.add("outcome", outcomes)
    analyticsReport.add(
        "tests",
        mapOf(
            "total" to testsSummary.total,
            "successful" to testsSummary.total - testsSummary.failures,
            "failed" to testsSummary.failures,
            "flaky" to testsSummary.flakes,
        )
    )
}

private suspend fun IArgs.runTests(): TestResult =
    when (this) {
        is AndroidArgs -> runAndroidTests()
        is IosArgs -> runIosTests()
        else -> throw FlankGeneralError("Unknown config type")
    }

private suspend fun <T> cancelTestsOnTimeout(
    projectId: String,
    savedMatrix: Map<String, TestMatrix.Data>? = null,
    block: suspend () -> T
) = try {
    block()
} catch (_: TimeoutCancellationException) {
    throw FlankTimeoutError(savedMatrix, projectId)
}
