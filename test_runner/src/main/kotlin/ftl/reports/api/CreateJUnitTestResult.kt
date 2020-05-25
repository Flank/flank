package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import ftl.reports.xml.model.JUnitTestResult

fun List<TestExecution>.createJUnitTestResult() = JUnitTestResult(
    testsuites = this
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .createJUnitTestSuites()
        .toMutableList()
)

fun List<TestExecution>.createJUnitTestResultForCi() = JUnitTestResult(
    testsuites = this
        .createTestExecutionDataListAsync()
        .prepareJUnitResultForCi()
        .createJUnitTestSuites()
        .toMutableList()
)
