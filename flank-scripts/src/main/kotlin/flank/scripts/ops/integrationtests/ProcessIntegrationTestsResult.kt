package flank.scripts.ops.integrationtests

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.common.config.fullSuiteWorkflowFilename
import flank.common.config.integrationOpenedIssueUser
import flank.scripts.data.github.commons.getLastWorkflowRunDate
import flank.scripts.data.github.getGitHubIssueList
import flank.scripts.ops.integrationtests.common.ITResult
import flank.scripts.ops.integrationtests.common.ITRunState
import flank.scripts.ops.integrationtests.common.IntegrationResultContext
import flank.scripts.ops.integrationtests.common.closeIssue
import flank.scripts.ops.integrationtests.common.createNewIssue
import flank.scripts.ops.integrationtests.common.postComment
import flank.scripts.ops.integrationtests.common.toITRunState
import flank.scripts.utils.toJson
import kotlinx.coroutines.runBlocking

fun processIntegrationTestsResult(
    result: ITResult,
    githubToken: String,
    runState: String,
    runID: String
) {
    val state = runState.toITRunState()
    logArgs(result, state.toJson(), runID)
    createContext(result, githubToken, state, runID).processIntegrationTestsResult()
}

private fun logArgs(
    result: ITResult,
    state: String,
    runID: String
) = println(
    """
    |** Parameters:
    |     global run result: $result
    |     run state: $state
    |     runID:  $runID
    """.trimMargin()
)

private fun createContext(
    result: ITResult,
    githubToken: String,
    itRunState: ITRunState,
    runID: String
) = IntegrationResultContext(
    result = result,
    token = githubToken,
    runState = itRunState,
    runID = runID,
    lastRun = runBlocking { getLastITWorkflowRunDate(githubToken) },
    openedIssue = runBlocking { checkForOpenedITIssues(githubToken) }
)

internal suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = fullSuiteWorkflowFilename
)

private fun IntegrationResultContext.processIntegrationTestsResult() = runBlocking {
    with(this) {
        when {
            result == ITResult.FAILURE && openedIssue == null -> createNewIssue()
            result == ITResult.FAILURE && openedIssue != null -> postComment()
            result == ITResult.SUCCESS && openedIssue != null -> closeIssue()
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
