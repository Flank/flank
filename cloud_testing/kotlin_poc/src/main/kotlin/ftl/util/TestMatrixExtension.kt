package ftl.util

import com.google.testing.model.TestMatrix
import com.google.testing.model.ToolResultsStep

fun TestMatrix.firstToolResults(): ToolResultsStep? {
    return this.testExecutions[0].toolResultsStep
}

fun TestMatrix.webLink(): String {
    val firstStep: ToolResultsStep = firstToolResults() ?: return ""
    return "https://console.firebase.google.com/project/${this.projectId}/" +
            "testlab/histories/${firstStep.historyId}/" +
            "matrices/${firstStep.executionId}"

}
