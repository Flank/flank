package ftl.reports.api

import com.google.api.services.toolresults.model.TestCase
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData

internal fun TestExecutionData.createTestSuitOverviewData(): TestSuiteOverviewData {
    val skipped = step.testExecutionStep.testSuiteOverviews.first().skippedCount ?: 0
    return TestSuiteOverviewData(
        total = testCases.size + skipped,
        errors = testCases.countErrors(),
        failures = testCases.countFailures(),
        flakes = testCases.countFlakes(),
        skipped = skipped
    )
}

private fun List<TestCase>.countErrors() = count { !it.flaky && it.status == "error" }
private fun List<TestCase>.countFailures() = count { !it.flaky && it.status == "failed" }
private fun List<TestCase>.countFlakes() = count { it.flaky }
