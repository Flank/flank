package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Step
import ftl.json.getDetails
import ftl.json.getOutcomeFromSteps
import ftl.util.StepOutcome

data class TestOutcome(
    val outcome: String,
    val matrixId: String,
    val testDetails: String,
    val billableVirtualMinutes: Long = 0,
    val billablePhysicalMinutes: Long = 0
)

fun List<Environment>.createMatrixOutcomeSummaryUsingEnvironments(
    testMatrixId: String
) =
    map { env ->
        TestOutcome(
            outcome = env.environmentResult.outcome.summary,
            matrixId = testMatrixId,
            testDetails = env.getDetails()
        )
    }.sortedBy {
        StepOutcome.order.indexOf(it.outcome)
    }

fun List<Step>.createMatrixOutcomeSummaryUsingSteps(
    testMatrixId: String
) =
    listOf(
        TestOutcome(
            outcome = getOutcomeFromSteps()?.summary ?: "Unknown",
            matrixId = testMatrixId,
            testDetails = getDetails()
        )
    )
