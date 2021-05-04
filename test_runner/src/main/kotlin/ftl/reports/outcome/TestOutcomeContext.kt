package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import com.google.testing.model.ToolResultsExecution
import ftl.gc.GcToolResults

data class TestOutcomeContext(
    val matrixId: String,
    val projectId: String,
    val environments: List<Environment>,
    val steps: List<Step>,
    val testTimeout: Long,
    val isRoboTest: Boolean
)

fun ftl.api.TestMatrix.Data.fetchTestOutcomeContext() = getToolResultsIds().let { ids ->
    TestOutcomeContext(
        projectId = projectId,
        matrixId = matrixId,
        environments = GcToolResults.listAllEnvironments(ids),
        steps = GcToolResults.listAllSteps(ids),
        testTimeout = testTimeout,
        isRoboTest = isRoboTest
    )
}

private fun ftl.api.TestMatrix.Data.getToolResultsIds(): ToolResultsExecution = ToolResultsExecution()
    .setProjectId(projectId)
    .setHistoryId(historyId)
    .setExecutionId(executionId)






