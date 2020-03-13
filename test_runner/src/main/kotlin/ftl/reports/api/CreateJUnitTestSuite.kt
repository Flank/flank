package ftl.reports.api

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestSuiteOverview
import ftl.reports.api.data.TestExecutionData
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestSuite

internal fun List<TestExecutionData>.createJUnitTestSuites() = map { data: TestExecutionData ->
    createJUnitTestSuite(
        data = data,
        overview = data.step
            .testExecutionStep
            .testSuiteOverviews
            .merge(),
        testCases = data.response
            .testCases
            .createJUnitTestCases()
    )
}

private fun createJUnitTestSuite(
    data: TestExecutionData,
    overview: TestSuiteOverview,
    testCases: List<JUnitTestCase>
) = JUnitTestSuite(
    name = data.step.testSuiteName(),
    testcases = testCases.toMutableList(),
    tests = overview.totalCount.format(),
    failures = overview.failureCount.format(),
    errors = overview.errorCount.format(),
    skipped = overview.skippedCount.format(),
    time = testCases.sumTime().format(),
    timestamp = data.testExecution.timestamp
)

private fun Step.testSuiteName(): String {
    val map = mutableMapOf<String, String>()
    this.dimensionValue.map { map[it.key] = it.value }
    return listOf(map["Model"], map["Version"], map["Locale"], map["Orientation"]).joinToString("-")
}

private fun List<JUnitTestCase>.sumTime() = this
    .map { it.time?.toDouble() ?: 0.0 }
    .reduce { acc, d -> acc + d }

private fun List<TestSuiteOverview>.merge(): TestSuiteOverview = if (isNotEmpty()) fold(
    // Use clone of first element as accumulator
    initial = first().clone().apply {
        errorCount = 0
        failureCount = 0
        skippedCount = 0
    }
) { accumulator, next ->
    accumulator.apply {
        errorCount += next.errorCount ?: 0
        failureCount += next.failureCount ?: 0
        skippedCount += next.skippedCount ?: 0
    }
} else throw IllegalArgumentException("List<TestSuiteOverview> cannot be empty.")
