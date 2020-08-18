package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.Step
import ftl.json.getDetails
import ftl.util.StepOutcome

data class TestOutcome(
    val outcome: String,
    val matrixId: String,
    val testDetails: String
)

fun List<Environment>.createMatrixOutcomeSummaryUsingEnvironments(
    testMatrixId: String,
    outcome: Outcome? = getOutcomeFromEnvironments(),
    testDetails: String? = outcome?.getDetails(map { it.createTestSuiteOverviewData() }.foldTestSuiteOverviewData())
) = TestOutcome(
    outcome = outcome?.summary ?: "Unknown",
    matrixId = testMatrixId,
    testDetails = testDetails ?: "Unknown outcome"
)

private fun List<Environment>.getOutcomeFromEnvironments(): Outcome? = maxBy {
    StepOutcome.order.indexOf(it.environmentResult?.outcome?.summary)
}?.environmentResult?.outcome

fun List<Step>.createMatrixOutcomeSummaryUsingSteps(
    testMatrixId: String,
    outcome: Outcome? = getOutcomeFromSteps(),
    testDetails: String? = outcome?.getDetails(createTestSuiteOverviewData())
) = TestOutcome(
    outcome = outcome?.summary ?: "Unknown",
    matrixId = testMatrixId,
    testDetails = testDetails ?: "Unknown outcome"
)

private fun List<Step>.getOutcomeFromSteps(): Outcome? = maxBy {
    StepOutcome.order.indexOf(it.outcome?.summary)
}?.outcome
