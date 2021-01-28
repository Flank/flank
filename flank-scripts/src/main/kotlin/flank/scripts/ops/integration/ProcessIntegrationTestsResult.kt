package flank.scripts.ops.integration

import kotlinx.coroutines.runBlocking

enum class ITResults {
    SUCCESS, FAILURE
}

fun processIntegrationTestsResult(
    result: ITResults,
    githubToken: String,
    url: String,
    runID: String
) {
    logArgs(result, url, runID)
    createContext(result, githubToken, url, runID).processIntegrationTestsResult()
}

private fun logArgs(
    result: ITResults,
    url: String,
    runID: String
) = println(
    """
    ** Parameters:
         result: $result
         url:    $url
         runID:  $runID
    """.trimIndent()
)

private fun createContext(
    result: ITResults,
    githubToken: String,
    url: String,
    runID: String
) = IntegrationContext(
    result = result,
    token = githubToken,
    url = url,
    runID = runID,
    lastRun = runBlocking { getLastITWorkflowRunDate(githubToken) },
    openedIssue = runBlocking { checkForOpenedITIssues(githubToken) }
)

private fun IntegrationContext.processIntegrationTestsResult() = runBlocking {
    with(this) {
        when {
            result == ITResults.FAILURE && openedIssue == null -> createNewIssue()
            result == ITResults.FAILURE && openedIssue != null -> postComment()
            result == ITResults.SUCCESS && openedIssue != null -> closeIssue()
            else -> return@with
        }
    }
}
