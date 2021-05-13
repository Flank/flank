package ftl.client.junit

import com.google.testing.model.TestExecution
import ftl.api.JUnitTest

internal fun List<TestExecution>.createJUnitTestResult() = JUnitTest.Result(
    testsuites = filterNullToolResultsStep()
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .createJUnitTestSuites()
        .toMutableList()
)

private fun List<TestExecution>.filterNullToolResultsStep() = filter { it.toolResultsStep != null }
