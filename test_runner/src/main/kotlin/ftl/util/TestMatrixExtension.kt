package ftl.util

import com.google.testing.model.TestMatrix
import com.google.testing.model.ToolResultsStep

fun TestMatrix.firstToolResults(): ToolResultsStep? {
    return this.testExecutions?.get(0)?.toolResultsStep
}

fun TestMatrix.webLink(): String {
    val numDevices = testExecutions?.size ?: 0
    val firstStep: ToolResultsStep = firstToolResults() ?: return ""

    val baseUrl =
        "https://console.firebase.google.com/project/${this.projectId}/" +
            "testlab/histories/${firstStep.historyId}/" +
            "matrices/${firstStep.executionId}"
    return if (numDevices == 1) {
        "$baseUrl/executions/${firstStep.stepId}"
    } else {
        baseUrl
    }
}

fun TestMatrix.getClientDetails(): Map<String, String>? =
    this.clientInfo?.clientInfoDetails?.associate { it.key to it.value }

fun TestMatrix.webLinkWithoutExecutionDetails(): String {
    val webLink = webLink()
    return if (webLink.isBlank()) {
        webLink
    } else {
        val executionsRegex = "/executions/.+".toRegex()
        val foundValue = executionsRegex.find(webLink)?.value.orEmpty()
        webLink.removeSuffix(foundValue)
    }
}

fun TestMatrix.getGcsPath() = this.resultStorage?.googleCloudStorage?.gcsPath ?: ""
fun TestMatrix.getGcsPathWithoutRootBucket() = this.getGcsPath().substringAfter('/')
fun TestMatrix.getGcsRootBucket() = this.getGcsPath().substringBefore('/')
