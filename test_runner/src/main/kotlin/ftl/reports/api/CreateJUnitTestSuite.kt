package ftl.reports.api

import com.google.api.services.toolresults.model.Step
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestSuite

internal fun List<TestExecutionData>.createJUnitTestSuites() = map { data: TestExecutionData ->
    createJUnitTestSuite(
        data = data,
        overview = data.createTestSuitOverviewData(),
        testCases = createJUnitTestCases(
            testCases = data.testCases,
            toolResultsStep = data.testExecution.toolResultsStep
        )
    )
}

private fun createJUnitTestSuite(
    data: TestExecutionData,
    overview: TestSuiteOverviewData,
    testCases: List<JUnitTestCase>
) = JUnitTestSuite(
    name = data.step.testSuiteName(),
    timestamp = data.timestamp.asUnixTimestamp().formatUtcDate(),
    tests = overview.total.toString(),
    failures = overview.failures.toString(),
    errors = overview.errors.toString(),
    skipped = overview.skipped.toString(),
    flakes = overview.flakes,
    testcases = testCases.toMutableList(),
    time = testCases.sumTime().format() // FIXME include also setup and teardown duration https://github.com/Flank/flank/issues/557
)

private fun Step.testSuiteName(): String {
    val map = dimensionValue.map { it.key to it.value }.toMap()
    return listOf(map["Model"], map["Version"], map["Locale"], map["Orientation"]).joinToString("-")
}

private fun List<JUnitTestCase>.sumTime() = this
    .map { it.time?.toDouble() ?: 0.0 }
    .reduce { acc, d -> acc + d }
