package ftl.reports.outcome

import com.google.api.services.toolresults.model.Environment

fun TestOutcomeContext.createMatrixOutcomeSummary(): Pair<BillableMinutes, List<TestOutcome>> =
    steps.calculateAndroidBillableMinutes(projectId, testTimeout) to when {
        environments.isNotEmpty() && environments.hasNoOutcome().not() ->
            environments.createMatrixOutcomeSummaryUsingEnvironments(matrixId)

        steps.isNotEmpty() -> {
            println("WARNING: Environment has no results, something went wrong. Displaying step outcomes instead.")
            steps.createMatrixOutcomeSummaryUsingSteps(matrixId)
        }

        else -> {
            println("No test results found, something went wrong. Try re-running the tests.")
            emptyList()
        }
    }

private fun List<Environment>.hasNoOutcome() = any { env ->
    env.environmentResult?.outcome?.summary == null
}
