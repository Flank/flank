package flank.scripts.integration

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

internal suspend fun IntegrationContext.createNewIssue(): GitHubCreateIssueCommentRequest =
    createAndPostNewIssue().postComment()

internal suspend fun IntegrationContext.postComment(): GitHubCreateIssueCommentRequest =
    createCommentPayload().also { payload ->
        postNewIssueComment(token, issueNumber, payload)
        println("** Comment posted")
        println(payload.toJson())
    }

internal suspend fun IntegrationContext.closeIssue(): ByteArray =
    postComment().run {
        println("** Closing issue")
        patchIssue(token, issueNumber, GitHubUpdateIssueRequest(state = IssueState.CLOSED)).get()
    }

private suspend fun IntegrationContext.createAndPostNewIssue(payload: GitHubCreateIssueRequest = createIssuePayload()) =
    postNewIssue(token, payload)
        .onError {
            println(it.message)
            exitProcess(-1)
        }
        .get()
        .run {
            logIssueCreated(this)
            copy(openedIssue = number)
        }

private fun createIssuePayload(): GitHubCreateIssueRequest {
    println("** Creating new issue")
    val issuePayload = GitHubCreateIssueRequest(
        title = "Full Suite integration tests failed on master",
        body = "### Integration Test failed on master",
        labels = listOf("IT_Failed", "bug")
    )
    println(issuePayload.toJson())
    return issuePayload
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

private fun logIssueCreated(issue: GitHubCreateIssueResponse) = println(
    """
** Issue created:
     url:    ${issue.htmlUrl}
     number: ${issue.number} 
""".trimIndent()
)
