package ftl.reports.api

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestSuiteOverview
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.StepOutcome

internal fun Environment.createTestSuitOverviewData(): TestSuiteOverviewData =
    shardSummaries.groupBy { it.shardResult?.outcome?.summary }.createTestSuitOverviewData()

internal fun List<Step>.createTestSuitOverviewData(): TestSuiteOverviewData =
    groupBy { it.outcome?.summary }.createTestSuitOverviewData()

private fun Map<String?, List<*>>.createTestSuitOverviewData(): TestSuiteOverviewData =
    let { outcomes ->
        TestSuiteOverviewData(
            total = outcomes.size,
            failures = outcomes[StepOutcome.failure]?.size ?: 0,
            flakes = outcomes[StepOutcome.flaky]?.size ?: 0,
            skipped = outcomes[StepOutcome.skipped]?.size ?: 0,
            errors = (outcomes - StepOutcome.notErrors).values.sumBy { it.size },
            elapsedTime = 0.0,
            overheadTime = 0.0
        )
    }

internal fun TestExecutionData.createTestSuitOverviewData(): TestSuiteOverviewData? = step
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

private fun List<TestCase>.sumTime(): Double = sumByDouble { it.elapsedTime.millis() }
