package ftl.reports.outcome

import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.ToolResultsExecution
import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import ftl.gc.GcToolResults
import ftl.util.timeoutToSeconds

data class TestOutcomeContext(
    val matrixId: String,
    val projectId: String,
    val environments: List<Environment>,
    val steps: List<Step>,
    val testTimeout: Long
)

fun TestMatrix.fetchTestOutcomeContext() = getToolResultsIds().let { ids ->
    TestOutcomeContext(
        projectId = projectId,
        matrixId = testMatrixId,
        environments = GcToolResults.listAllEnvironments(ids),
        steps = GcToolResults.listAllSteps(ids),
        testTimeout = testTimeout()
    )
}

private fun TestMatrix.getToolResultsIds(): ToolResultsExecution = ToolResultsExecution()
    .setProjectId(projectId)
    .setHistoryId(resultStorage?.toolResultsExecution?.historyId ?: throw BadMatrixError())
    .setExecutionId(resultStorage?.toolResultsExecution?.executionId ?: throw BadMatrixError())

class BadMatrixError : Exception()

private fun TestMatrix.testTimeout() = timeoutToSeconds(
    testExecutions.firstOrNull {
        it?.testSpecification?.testTimeout != null
    testExecutions
         .firstOrNull { it?.testSpecification?.testTimeout != null }
         ?.testSpecification
         ?.testTimeout 
         ?: "0s"
)
