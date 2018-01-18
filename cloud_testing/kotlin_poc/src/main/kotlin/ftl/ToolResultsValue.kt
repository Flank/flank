package ftl

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.StepDimensionValueEntry
import ftl.Billing.billableMinutes
import ftl.Constants.projectId

internal class ToolResultsValue(step: Step, toolResultsStep: ToolResultsStepGcsPath) {
    var webLink: String = ""
    var billableMinutes: Long = 0
    var testDurationSeconds: Long = 0
    var runDurationSeconds: Long = 0
    var name: String
    var targets: String
    var dimensions: List<StepDimensionValueEntry>
    var outcome: String
    private val step = toolResultsStep.toolResults
    val gcsPath = toolResultsStep.gcsPath

    init {
        updateWebLink()
        billableMinutes = billableMinutes(testDurationSeconds)

        val executionStep = step.testExecutionStep
        testDurationSeconds = executionStep.testTiming.testProcessDuration.seconds!!

        runDurationSeconds = step.runDuration.seconds!!
        name = step.name
        targets = step.description
        dimensions = step.dimensionValue
        // "failure" or "success"
        outcome = step.outcome.summary
    }

    private fun updateWebLink() {
        webLink = "https://console.firebase.google.com/project/$projectId/" +
                "testlab/histories/${step.historyId}/" +
                "matrices/${step.executionId}"
    }

    override fun toString(): String {
        return """Billable / test / run: ${billableMinutes}m / ${testDurationSeconds}s / ${runDurationSeconds}s
Outcome: $outcome
$webLink"""
    }
}
