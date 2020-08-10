package ftl.json

import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.ToolResultsExecution
import com.google.api.services.toolresults.model.Environment
import ftl.gc.GcToolResults
import ftl.util.StepOutcome

data class TestOutcome(
    val outcome: String,
    val matrixId: String,
    val testDetails: String,
    val billableVirtualMinutes: Int = 0,
    val billablePhysicalMinutes: Int = 0
)

fun TestMatrix.createMatrixOutcomeSummary(): TestOutcome? = createMatrixOutcomeSummaryUsingEnvironments(getToolResultsIds())

private fun TestMatrix.createMatrixOutcomeSummaryUsingEnvironments(ids: ToolResultsExecution): TestOutcome? =
    GcToolResults.listAllEnvironments(ids).run {
        if (isEmpty() || hasNoOutcome()) {
            println("WARNING: Environment has no results, something went wrong. Displaying step outcomes instead.")
            createMatrixOutcomeSummaryUsingSteps(ids)
        } else map { env ->
            TestOutcome(
                outcome = env.environmentResult.outcome.summary,
                matrixId = testMatrixId,
                testDetails = env.getDetails()
            )
        }.minBy {
            StepOutcome.order.indexOf(it.outcome)
        }
    }

private fun TestMatrix.getToolResultsIds() = ToolResultsExecution()
    .setProjectId(projectId)
    .setHistoryId(resultStorage?.toolResultsExecution?.historyId ?: throw BadMatrixError())
    .setExecutionId(resultStorage?.toolResultsExecution?.executionId ?: throw BadMatrixError())

private fun List<Environment>.hasNoOutcome() = any { env ->
    env.environmentResult?.outcome?.summary != null
}

private fun TestMatrix.createMatrixOutcomeSummaryUsingSteps(ids: ToolResultsExecution): TestOutcome? =
    GcToolResults.listAllSteps(ids).run {
        if (isEmpty()) {
            println("No test results found, something went wrong. Try re-running the tests.")
            null
        } else TestOutcome(
            outcome = getOutcome()?.summary ?: "Unknown",
            matrixId = testMatrixId,
            testDetails = getDetails()
        )
    }

class BadMatrixError : Exception()
