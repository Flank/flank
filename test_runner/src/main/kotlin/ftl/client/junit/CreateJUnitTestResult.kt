package ftl.client.junit

import com.google.testing.model.TestExecution

internal fun List<TestExecution>.createJUnitTestResult() = JUnitTestResult(
    testsuites = filterNullToolResultsStep()
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .createJUnitTestSuites()
        .toMutableList()
)

private fun List<TestExecution>.filterNullToolResultsStep() = filter { it.toolResultsStep != null }
