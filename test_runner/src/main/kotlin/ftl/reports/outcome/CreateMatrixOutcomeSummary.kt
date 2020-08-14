package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment

fun TestOutcomeContext.createMatrixOutcomeSummary(): Pair<BillableMinutes, TestOutcome> =
    steps.calculateAndroidBillableMinutes(projectId, testTimeout) to when {
        environments.isNotEmpty() && environments.hasNoOutcome().not() ->
            environments.createMatrixOutcomeSummaryUsingEnvironments(matrixId)

        else -> {
            if (steps.isEmpty()) println("No test results found, something went wrong. Try re-running the tests.")
            steps.createMatrixOutcomeSummaryUsingSteps(matrixId)
        }
    }

private fun List<Environment>.hasNoOutcome() = any { env ->
    env.environmentResult?.outcome?.summary == null
}
