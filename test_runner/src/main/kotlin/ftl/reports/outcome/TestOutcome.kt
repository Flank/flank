package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.Step
import ftl.json.getDetails
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.StepOutcome

data class TestOutcome(
    val device: String = "",
    val outcome: String = "",
    val details: String = "",
    val testSuiteOverview: TestSuiteOverviewData = TestSuiteOverviewData()
)

fun TestOutcomeContext.createMatrixOutcomeSummaryUsingEnvironments(): List<TestOutcome> = environments
    .map { environment ->
        TestOutcome(
            device = environment.axisValue(),
            outcome = environment.outcomeSummary,
            details = environment.getOutcomeDetails(isRoboTest),
            testSuiteOverview = environment.createTestSuiteOverviewData()
        )
    }

private val Environment.outcomeSummary
    get() = environmentResult?.outcome?.summary ?: UNKNOWN_OUTCOME

private fun Environment.getOutcomeDetails(isRoboTest: Boolean) = environmentResult?.outcome.getDetails(createTestSuiteOverviewData(), isRoboTest)

fun TestOutcomeContext.createMatrixOutcomeSummaryUsingSteps() = steps
    .groupBy(Step::axisValue)
    .map { (device, steps) ->
        TestOutcome(
            device = device,
            outcome = steps.getOutcomeSummary(),
            details = steps.getOutcomeDetails(isRoboTest),
            testSuiteOverview = steps.createTestSuiteOverviewData()
        )
    }

private fun List<Step>.getOutcomeSummary() = getOutcomeFromSteps()?.summary ?: UNKNOWN_OUTCOME

private fun List<Step>.getOutcomeDetails(isRoboTest: Boolean) = getOutcomeFromSteps().getDetails(createTestSuiteOverviewData(), isRoboTest)

private fun List<Step>.getOutcomeFromSteps(): Outcome? = maxByOrNull {
    StepOutcome.order.indexOf(it.outcome?.summary)
}?.outcome

private const val UNKNOWN_OUTCOME = "Unknown"
