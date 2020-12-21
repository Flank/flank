package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment
import flank.common.logLn

fun TestOutcomeContext.createMatrixOutcomeSummary() = billableMinutes() to outcomeSummary()

private fun TestOutcomeContext.billableMinutes() = steps.calculateAndroidBillableMinutes(projectId, testTimeout)

private fun TestOutcomeContext.outcomeSummary() =
    if (environments.hasOutcome())
        createMatrixOutcomeSummaryUsingEnvironments()
    else {
        if (steps.isEmpty()) logLn("No test results found, something went wrong. Try re-running the tests.")
        createMatrixOutcomeSummaryUsingSteps()
    }

private fun List<Environment>.hasOutcome() = isNotEmpty() && all { it.environmentResult?.outcome?.summary != null }
