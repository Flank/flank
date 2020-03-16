package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import ftl.gc.GcToolResults
import ftl.reports.api.data.TestExecutionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

internal fun List<TestExecution>.createTestExecutionDataListAsync(): List<TestExecutionData> = runBlocking {
    map { testExecution ->
        async(Dispatchers.IO) {
            // calling this function in async block, speeds up execution because
            // it is making api calls under the hood
            testExecution.createTestExecutionData()
        }
    }.awaitAll()
}

private fun TestExecution.createTestExecutionData() = TestExecutionData(
    testExecution = this,
    response = GcToolResults.listTestCases(toolResultsStep),
    step = GcToolResults.getStepResult(toolResultsStep)
)
