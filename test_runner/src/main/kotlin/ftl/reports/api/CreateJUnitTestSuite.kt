package ftl.reports.api

import com.google.api.services.toolresults.model.Step
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.xml.model.JUnitTestSuite

internal fun List<TestExecutionData>.createJUnitTestSuites() = map { data: TestExecutionData ->
    createJUnitTestSuite(
        data = data,
        overview = data.createTestSuitOverviewData()
    )
}

private fun createJUnitTestSuite(
    data: TestExecutionData,
    overview: TestSuiteOverviewData
) = JUnitTestSuite(
    name = data.step.testSuiteName(),
    timestamp = data.timestamp.asUnixTimestamp().formatUtcDate(),
    tests = overview.total.toString(),
    failures = overview.failures.toString(),
    errors = overview.errors.toString(),
    skipped = overview.skipped.toString(),
    flakes = overview.flakes,
    testcases = createJUnitTestCases(
        testCases = data.testCases,
        toolResultsStep = data.testExecution.toolResultsStep,
        overheadTime = overview.overheadTime
    ).toMutableList(),
    time = overview.elapsedTime.format()
)

private fun Step.testSuiteName(): String {
    val map = dimensionValue.map { it.key to it.value }.toMap()
    return listOf(map["Model"], map["Version"], map["Locale"], map["Orientation"]).joinToString("-")
}
