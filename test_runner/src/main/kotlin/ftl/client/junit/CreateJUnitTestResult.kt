package ftl.client.junit

import com.google.testing.model.TestExecution
import ftl.api.JUnitTest
import ftl.domain.junit.merge

internal fun List<TestExecution>.createJUnitTestResult(): JUnitTest.Result = JUnitTest.Result(
    testsuites = filterNullToolResultsStep()
        .createTestExecutionDataListAsync()
        .prepareForJUnitResult()
        .createJUnitTestSuites()
        .mergeResults()
        .toMutableList()
)

private fun List<JUnitTest.Suite>.mergeResults(): List<JUnitTest.Suite> = groupBy { it.name }
    .values
    .map { it.reduce { acc, suite -> acc.merge(suite) } }

private fun List<TestExecution>.filterNullToolResultsStep() = filter { it.toolResultsStep != null }
