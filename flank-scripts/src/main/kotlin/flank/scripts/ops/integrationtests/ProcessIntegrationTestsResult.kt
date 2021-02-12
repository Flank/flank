package flank.scripts.ops.integrationtests

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.common.config.fullSuiteWorkflowFilename
import flank.common.config.integrationOpenedIssueUser
import flank.scripts.data.github.commons.getLastWorkflowRunDate
import flank.scripts.data.github.getGitHubIssueList
import flank.scripts.ops.integrationtests.common.ITResults
import flank.scripts.ops.integrationtests.common.IntegrationContext
import flank.scripts.ops.integrationtests.common.closeIssue
import flank.scripts.ops.integrationtests.common.createNewIssue
import flank.scripts.ops.integrationtests.common.postComment
import kotlinx.coroutines.runBlocking

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

internal suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = fullSuiteWorkflowFilename
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

internal suspend fun checkForOpenedITIssues(token: String): Int? = getGitHubIssueList(
    githubToken = token,
    parameters = listOf(
        "creator" to integrationOpenedIssueUser,
        "state" to "open",
        "labels" to "IT_Failed"
    )
)
    .onError { println(it.message) }
    .getOrElse { emptyList() }
    .firstOrNull()
    .also {
        if (it != null) println("** Issue found: ${it.htmlUrl}")
        else println("** No opened issue")
    }?.number
