package ftl.client.junit

import ftl.api.JUnitTest
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.api.format
import ftl.reports.outcome.axisValue

internal fun List<TestExecutionData>.createJUnitTestSuites() = mapNotNull { data: TestExecutionData ->
    data.createTestSuiteOverviewData()?.let { overviewData ->
        createJUnitTestSuite(
            data = data,
            overview = overviewData
        )
    }
}

private fun createJUnitTestSuite(
    data: TestExecutionData,
    overview: TestSuiteOverviewData
) = JUnitTest.Suite(
    name = data.step.axisValue(),
    timestamp = data.timestamp.asUnixTimestamp().formatUtcDate(),
    tests = overview.total.toString(),
    failures = overview.failures.toString(),
    errors = overview.errors.toString(),
    skipped = overview.skipped.toString(),
    flakes = overview.flakes,
    testcases = createJUnitTestCases(
        testCases = data.testCases,
        toolResultsStep = data.testExecution.toolResultsStep,
    ).toMutableList(),
    time = overview.elapsedTime.format()
)
