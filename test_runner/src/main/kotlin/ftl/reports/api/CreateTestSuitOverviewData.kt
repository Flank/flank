package ftl.reports.api

import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestSuiteOverview
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData

internal fun TestExecutionData.createTestSuitOverviewData(): TestSuiteOverviewData {
    val overview: TestSuiteOverview = step.testExecutionStep.testSuiteOverviews.first()
    val skipped: Int = overview.skippedCount ?: 0
    return TestSuiteOverviewData(
        total = testCases.size + skipped,
        errors = testCases.countErrors(),
        failures = testCases.countFailures(),
        flakes = testCases.countFlakes(),
        skipped = skipped,
        elapsedTime = overview.elapsedTime.millis(),
        overheadTime = getOverheadTime(overview, testCases)
    )
}

private fun List<TestCase>.countErrors() = count { !it.flaky && it.status == "error" }
private fun List<TestCase>.countFailures() = count { !it.flaky && it.status == "failed" }
private fun List<TestCase>.countFlakes() = count { it.flaky }

private fun getOverheadTime(
    overview: TestSuiteOverview,
    testCases: List<TestCase>
) = if (testCases.isEmpty())
    0.0 else
    (overview.elapsedTime.millis() - testCases.sumTime()) / testCases.size

private fun List<TestCase>.sumTime(): Double = this
    .map { it.elapsedTime.millis() }
    .reduce { acc, d -> acc + d }
