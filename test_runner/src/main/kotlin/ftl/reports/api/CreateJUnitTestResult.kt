package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import ftl.reports.xml.model.JUnitTestResult

internal fun List<TestExecution>.createJUnitTestResult(
    withStackTraces: Boolean = false
) = JUnitTestResult(
    testsuites = this
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .let { executionDataList ->
            if (withStackTraces) executionDataList
            else executionDataList.removeStackTraces()
        }
        .createJUnitTestSuites()
        .toMutableList()
)
