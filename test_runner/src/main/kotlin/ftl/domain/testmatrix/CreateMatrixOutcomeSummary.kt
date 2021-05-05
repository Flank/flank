package ftl.domain.testmatrix

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.Step
import com.google.common.annotations.VisibleForTesting
import flank.common.logLn
import ftl.api.TestMatrix
import ftl.json.getDetails
import ftl.reports.outcome.BillableMinutes
import ftl.reports.outcome.TestOutcome
import ftl.reports.outcome.axisValue
import ftl.reports.outcome.calculateAndroidBillableMinutes
import ftl.reports.outcome.createTestSuiteOverviewData
import ftl.util.StepOutcome

// TODO Piotrek adapter

fun fetchMatrixOutcome(newMatrix: TestMatrix.Data) = newMatrix.fetchTestOutcomeContext().createMatrixOutcomeSummary()

@VisibleForTesting
internal fun TestOutcomeContext.createMatrixOutcomeSummary(): Pair<BillableMinutes, List<TestOutcome>> =
    billableMinutes() to outcomeSummary()

private fun TestOutcomeContext.billableMinutes() = steps.calculateAndroidBillableMinutes(projectId, testTimeout)

private fun TestOutcomeContext.outcomeSummary(): List<TestOutcome> =
    if (environments.hasOutcome())
        createMatrixOutcomeSummaryUsingEnvironments()
    else {
        if (steps.isEmpty()) logLn("No test results found, something went wrong. Try re-running the tests.")
        createMatrixOutcomeSummaryUsingSteps()
    }

private fun List<Environment>.hasOutcome() = isNotEmpty() && all { it.environmentResult?.outcome?.summary != null }

private fun TestOutcomeContext.createMatrixOutcomeSummaryUsingEnvironments(): List<TestOutcome> = environments
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

private fun Environment.getOutcomeDetails(isRoboTest: Boolean) =
    environmentResult?.outcome.getDetails(createTestSuiteOverviewData(), isRoboTest)

private fun TestOutcomeContext.createMatrixOutcomeSummaryUsingSteps() = steps
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

private fun List<Step>.getOutcomeDetails(isRoboTest: Boolean) =
    getOutcomeFromSteps().getDetails(createTestSuiteOverviewData(), isRoboTest)

private fun List<Step>.getOutcomeFromSteps(): Outcome? = maxByOrNull {
    StepOutcome.order.indexOf(it.outcome?.summary)
}?.outcome

private const val UNKNOWN_OUTCOME = "Unknown"
