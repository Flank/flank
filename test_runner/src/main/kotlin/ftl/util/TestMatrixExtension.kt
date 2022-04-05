package ftl.util

import com.google.testing.model.TestMatrix
import com.google.testing.model.ToolResultsStep

fun TestMatrix.firstToolResults(): ToolResultsStep? {
    return this.testExecutions?.get(0)?.toolResultsStep
}

fun TestMatrix.webLink(): String {
    val firstStep: ToolResultsStep = firstToolResults() ?: return ""

    return "https://console.firebase.google.com/project/${this.projectId}/" +
        "testlab/histories/${firstStep.historyId}/" +
        "matrices/${firstStep.executionId}/details"
}

fun TestMatrix.getClientDetails(): Map<String, String>? =
    this.clientInfo?.clientInfoDetails?.associate { it.key to it.value }
fun TestMatrix.getGcsPath() = this.resultStorage?.googleCloudStorage?.gcsPath ?: ""
fun TestMatrix.getGcsPathWithoutRootBucket() = this.getGcsPath().substringAfter('/')
fun TestMatrix.getGcsRootBucket() = this.getGcsPath().substringBefore('/')
