package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment

fun TestOutcomeContext.createMatrixOutcomeSummary() = Pair(
    steps.calculateAndroidBillableMinutes(projectId, testTimeout),
    if (environments.hasOutcome())
        createMatrixOutcomeSummaryUsingEnvironments()
    else {
        if (steps.isEmpty()) println("No test results found, something went wrong. Try re-running the tests.")
        createMatrixOutcomeSummaryUsingSteps()
    }
)

private fun List<Environment>.hasOutcome() = isNotEmpty() && all { it.environmentResult?.outcome?.summary != null }
