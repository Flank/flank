package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestSuiteOverview
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.api.millis

internal fun Environment.createTestSuitOverviewData(): TestSuiteOverviewData =
    environmentResult.testSuiteOverviews.fold(TestSuiteOverviewData()) { acc, overview ->
        acc.copy(
            total = acc.total + (overview.totalCount ?: 0),
            failures = acc.failures + (overview.failureCount ?: 0),
            flakes = acc.flakes + (overview.flakyCount ?: 0),
            skipped = acc.skipped + (overview.skippedCount ?: 0),
            errors = acc.errors + (overview.errorCount ?: 0),
            elapsedTime = acc.elapsedTime + overview.elapsedTime.millis()
        )
    }

internal fun List<Step>.createTestSuitOverviewData(): TestSuiteOverviewData = this
    .also { require(isNotEmpty()) }
    .filter(Step::isPrimaryStep)
    .groupBy(Step::deviceModel)
    .values
    .map { it.mapToTestSuiteOverviews().foldTestSuiteOverviewData() }
    .maxBy { it.flakes + it.errors + it.failures }!!

private fun Step.isPrimaryStep() =
    multiStep?.primaryStep?.rollUp != null || multiStep == null

private fun List<Step>.mapToTestSuiteOverviews() = mapNotNull {
    it.testExecutionStep.testSuiteOverviews.firstOrNull()
}

private fun List<TestSuiteOverview>.foldTestSuiteOverviewData() =
    fold(TestSuiteOverviewData()) { acc, overview -> acc + overview }
