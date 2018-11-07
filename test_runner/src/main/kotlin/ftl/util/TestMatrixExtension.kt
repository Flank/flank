package ftl.util

import com.google.api.services.testing.model.TestMatrix
import com.google.api.services.testing.model.ToolResultsStep

fun TestMatrix.firstToolResults(): ToolResultsStep? {
    return this.testExecutions?.get(0)?.toolResultsStep
}

fun TestMatrix.webLink(): String {
    val numDevices = testExecutions?.size ?: 0
    val firstStep: ToolResultsStep = firstToolResults() ?: return ""

    val baseUrl =
        "https://console.firebase.google.com/project/${this.projectId}/testlab/histories/${firstStep.historyId}"
    return if (numDevices == 1) {
        "$baseUrl/executions/${firstStep.stepId}"
    } else {
        baseUrl
    }
}
