package ftl.client.junit

import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestSuiteOverview
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.api.millis

internal fun TestExecutionData.createTestSuiteOverviewData(): TestSuiteOverviewData? = step
    .testExecutionStep
    .testSuiteOverviews
    ?.firstOrNull()
    ?.let { overview ->
        val skipped: Int = overview.skippedCount ?: 0
        TestSuiteOverviewData(
            total = testCases.size,
            errors = testCases.countErrors(),
            failures = testCases.countFailures(),
            flakes = testCases.countFlakes(),
            skipped = skipped,
            elapsedTime = overview.elapsedTime.millis(),
        )
    }

private fun List<TestCase>.countErrors() = count { !it.flaky && it.status == "error" }
private fun List<TestCase>.countFailures() = count { !it.flaky && it.status == "failed" }
private fun List<TestCase>.countFlakes() = count { it.flaky }
