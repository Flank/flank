package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.Step
import ftl.json.getDetails
import ftl.util.StepOutcome

data class TestOutcome(
    val device: String = "",
    val outcome: String = "",
    val details: String = "",
)

fun List<Environment>.createMatrixOutcomeSummaryUsingEnvironments(): List<TestOutcome> =
    map(Environment::getTestOutcome)

private fun Environment.getTestOutcome(
    outcome: Outcome? = environmentResult?.outcome
) = TestOutcome(
    device = deviceModel(),
    outcome = outcome?.summary ?: UNKNOWN_OUTCOME,
    details = outcome.getDetails(createTestSuiteOverviewData()),
)

fun List<Step>.createMatrixOutcomeSummaryUsingSteps() = groupBy(Step::deviceModel).map { (device, steps) ->
    steps.getTestOutcome(device)
}

private fun List<Step>.getTestOutcome(
    deviceModel: String,
    outcome: Outcome? = getOutcomeFromSteps(),
) = TestOutcome(
    device = deviceModel,
    outcome = outcome?.summary ?: UNKNOWN_OUTCOME,
    details = outcome.getDetails(createTestSuiteOverviewData())
)

private fun List<Step>.getOutcomeFromSteps(): Outcome? = maxByOrNull {
    StepOutcome.order.indexOf(it.outcome?.summary)
}?.outcome

private const val UNKNOWN_OUTCOME = "Unknown"
