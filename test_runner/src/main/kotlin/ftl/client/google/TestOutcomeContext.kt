package ftl.domain.testmatrix

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import com.google.testing.model.ToolResultsExecution
import ftl.api.TestMatrix
import ftl.gc.GcToolResults

// TODO Piotrek client

data class TestOutcomeContext(
    val matrixId: String,
    val projectId: String,
    val environments: List<Environment>,
    val steps: List<Step>,
    val testTimeout: Long,
    val isRoboTest: Boolean
)

fun TestMatrix.Data.fetchTestOutcomeContext(): TestOutcomeContext = getToolResultsIds().let { ids ->
    TestOutcomeContext(
        projectId = projectId,
        matrixId = matrixId,
        environments = GcToolResults.listAllEnvironments(ids),
        steps = GcToolResults.listAllSteps(ids),
        testTimeout = testTimeout,
        isRoboTest = isRoboTest
    )
}

private fun TestMatrix.Data.getToolResultsIds(): ToolResultsExecution = ToolResultsExecution()
    .setProjectId(projectId)
    .setHistoryId(historyId)
    .setExecutionId(executionId)
