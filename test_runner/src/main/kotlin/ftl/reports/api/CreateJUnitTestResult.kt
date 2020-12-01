package ftl.reports.api

import com.google.testing.model.TestExecution
import ftl.reports.xml.model.JUnitTestResult

internal fun List<TestExecution>.createJUnitTestResult(
    withStackTraces: Boolean = false
) = JUnitTestResult(
    testsuites = this
        .filterNullToolResultsStep()
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .let { executionDataList ->
            if (withStackTraces) executionDataList
            else executionDataList.removeStackTraces()
        }
        .createJUnitTestSuites()
        .toMutableList()
)

private fun List<TestExecution>.filterNullToolResultsStep() = filter { it.toolResultsStep != null }
