package flank.scripts.integration

import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import flank.scripts.github.objects.GitHubCreateIssueCommentRequest
import flank.scripts.github.objects.GitHubCreateIssueRequest
import flank.scripts.github.objects.GitHubCreateIssueResponse
import flank.scripts.github.objects.GitHubUpdateIssueRequest
import flank.scripts.github.objects.IssueState
import flank.scripts.github.patchIssue
import flank.scripts.github.postNewIssue
import flank.scripts.github.postNewIssueComment
import flank.scripts.utils.toJson
import kotlinx.coroutines.coroutineScope
import kotlin.system.exitProcess

internal suspend fun IntegrationContext.createNewIssue() = coroutineScope {
    println("** Creating new issue")
    val issuePayload = GitHubCreateIssueRequest(
        title = "Full Suite integration tests failed on master",
        body = "### Integration Test failed on master",
        labels = listOf("IT_Failed", "bug")
    )
    println(issuePayload.toJson())
    val issue = postNewIssue(token, issuePayload)
        .onError {
            println(it.message)
        }
        .getOrNull()
    if (issue == null) {
        println("** Issue created but empty response returned. Terminating.")
        exitProcess(-1)
    }
    logIssueCreated(issue)
    copy(openedIssue = issue.number)
}.postComment()

internal suspend fun IntegrationContext.postComment() = createCommentPayload().also { payload ->
    postNewIssueComment(token, issueNumber, payload)
    println("** Comment posted")
    println(payload.toJson())
}

private suspend fun IntegrationContext.createCommentPayload() = coroutineScope {
    val message = when (result) {
        ITResults.SUCCESS -> prepareSuccessMessage(lastRun, runID, url)
        ITResults.FAILURE -> {
            val commitList = getCommitListSinceDate(token, lastRun)
            prepareFailureMessage(lastRun, runID, url, commitList)
        }
    }
    GitHubCreateIssueCommentRequest(message)
}

internal suspend fun IntegrationContext.closeIssue() = postComment().also {
    println("** Closing issue")
    patchIssue(token, issueNumber, GitHubUpdateIssueRequest(state = IssueState.CLOSED))
}

private fun logIssueCreated(issue: GitHubCreateIssueResponse) = println(
    """
** Issue created:
     url:    ${issue.htmlUrl}
     number: ${issue.number} 
""".trimIndent()
)
