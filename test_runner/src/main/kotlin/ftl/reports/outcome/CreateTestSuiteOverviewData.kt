package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.api.millis

internal fun Environment.createTestSuiteOverviewData(): TestSuiteOverviewData =
    environmentResult?.testSuiteOverviews?.fold(TestSuiteOverviewData()) { acc, overview ->
        acc.copy(
            total = acc.total + (overview.totalCount ?: 0),
            failures = acc.failures + (overview.failureCount ?: 0),
            flakes = acc.flakes + (overview.flakyCount ?: 0),
            skipped = acc.skipped + (overview.skippedCount ?: 0),
            errors = acc.errors + (overview.errorCount ?: 0),
            elapsedTime = acc.elapsedTime + overview.elapsedTime.millis()
        )
    } ?: TestSuiteOverviewData()

internal fun List<Step>.createTestSuiteOverviewData(): TestSuiteOverviewData = this
    .also { require(isNotEmpty()) }
    .filter(Step::isPrimaryStep)
    .groupBy(Step::axisValue)
    .values
    .map { it.mapToTestSuiteOverviews().foldTestSuiteOverviewData() }
    .fold(TestSuiteOverviewData()) { acc, data -> acc + data }

private fun Step.isPrimaryStep() =
    multiStep?.primaryStep?.rollUp != null || multiStep == null

private fun List<Step>.mapToTestSuiteOverviews() = mapNotNull {
    it.testExecutionStep?.testSuiteOverviews?.firstOrNull()?.let { overview -> TestSuiteOverviewData() + overview }
}

internal fun List<TestSuiteOverviewData>.foldTestSuiteOverviewData() =
    fold(TestSuiteOverviewData()) { acc, data -> acc + data }
