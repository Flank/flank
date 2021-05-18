package flank.scripts.ops.integrationtests.common

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.scripts.data.github.getGitHubCommitList
import flank.scripts.data.github.getPrDetailsByCommit
import flank.scripts.data.github.objects.GitHubCreateIssueCommentRequest
import flank.scripts.data.github.objects.GitHubCreateIssueRequest
import flank.scripts.data.github.objects.GitHubCreateIssueResponse
import flank.scripts.data.github.objects.GitHubUpdateIssueRequest
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.data.github.objects.IssueState
import flank.scripts.data.github.patchIssue
import flank.scripts.data.github.postNewIssue
import flank.scripts.data.github.postNewIssueComment
import flank.scripts.utils.toJson
import flank.scripts.utils.toObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.system.exitProcess

internal suspend fun IntegrationResultContext.createNewIssue(): GitHubCreateIssueCommentRequest =
    createAndPostNewIssue().postComment()

internal suspend fun IntegrationResultContext.postComment(): GitHubCreateIssueCommentRequest =
    createCommentPayload().also { payload ->
        postNewIssueComment(token, issueNumber, payload)
        println("** Comment posted")
        println(payload.toJson())
    }

internal suspend fun IntegrationResultContext.closeIssue(): ByteArray =
    postComment().run {
        println("** Closing issue")
        patchIssue(token, issueNumber, GitHubUpdateIssueRequest(state = IssueState.CLOSED)).get()
    }

private suspend fun IntegrationResultContext.createAndPostNewIssue(payload: GitHubCreateIssueRequest = createIssuePayload()) =
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

private suspend fun IntegrationResultContext.createCommentPayload() = coroutineScope {
    val message = when (result) {
        ITResult.SUCCESS -> prepareSuccessMessage(lastRun, runID, runState)
        ITResult.FAILURE -> {
            val commitList = getCommitListSinceDate(token, lastRun)
            prepareFailureMessage(lastRun, runID, runState, commitList)
        }
    }
    GitHubCreateIssueCommentRequest(message)
}

internal suspend fun getCommitListSinceDate(
    token: String,
    since: String
): List<Pair<String, GithubPullRequest?>> = coroutineScope {
    getGitHubCommitList(token, listOf("since" to since))
        .onError { println(it.message) }
        .getOrElse { emptyList() }
        .map {
            async {
                it.sha to getPrDetailsByCommit(it.sha, token).getOrElse { emptyList() }
            }
        }
        .awaitAll()
        .flatMap { (commit, prs) ->
            if (prs.isEmpty()) listOf(commit to null)
            else prs.map { commit to it }
        }
        .toList()
}

private fun logIssueCreated(issue: GitHubCreateIssueResponse) = println(
    """
** Issue created:
     url:    ${issue.htmlUrl}
     number: ${issue.number} 
    """.trimIndent()
)

data class IntegrationResultContext(
    val result: ITResult,
    val token: String,
    val runState: ITRunState,
    val runID: String,
    val lastRun: String,
    val openedIssue: Int?,
) {
    val issueNumber: Int
        get() = requireNotNull(openedIssue)
}

@Serializable
data class ITRunState(
    @SerialName("Windows")
    val windowsResult: ITResult = ITResult.FAILURE,
    @SerialName("Windows-bs")
    val windowsBSUrl: String = "",
    @SerialName("macOS")
    val macOsResult: ITResult = ITResult.FAILURE,
    @SerialName("macOS-bs")
    val macOsBSUrl: String = "",
    @SerialName("Linux")
    val linuxResult: ITResult = ITResult.FAILURE,
    @SerialName("Linux-bs")
    val linuxBSUrl: String = ""
)

internal fun String.toITRunState() = this.toObject<ITRunState>()
