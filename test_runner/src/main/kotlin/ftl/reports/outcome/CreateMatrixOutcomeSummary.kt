package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment

fun TestOutcomeContext.createMatrixOutcomeSummary(): Pair<BillableMinutes, TestOutcome> =
    steps.calculateAndroidBillableMinutes(projectId, testTimeout) to
        if (environments.hasOutcome())
            environments.createMatrixOutcomeSummaryUsingEnvironments()
        else {
            if (steps.isEmpty()) println("No test results found, something went wrong. Try re-running the tests.")
            steps.createMatrixOutcomeSummaryUsingSteps()
        }

private fun List<Environment>.hasOutcome() = isNotEmpty() && any { env ->
    env.environmentResult?.outcome?.summary == null
}.not()
