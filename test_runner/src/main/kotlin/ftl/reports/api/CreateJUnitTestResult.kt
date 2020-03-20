package ftl.reports.api

import com.google.api.services.testing.model.TestMatrix
import ftl.reports.xml.model.JUnitTestResult

internal fun TestMatrix.createJUnitTestResult() = JUnitTestResult(
    testsuites = testExecutions
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .createJUnitTestSuites()
        .toMutableList()
)
