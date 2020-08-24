package ftl.reports.api

import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.outcome.deviceModel
import ftl.reports.xml.model.JUnitTestSuite

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
) = JUnitTestSuite(
    name = data.step.deviceModel(),
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
