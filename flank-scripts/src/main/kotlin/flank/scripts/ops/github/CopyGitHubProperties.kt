package flank.scripts.ops.github

import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.data.github.getGitHubIssue
import flank.scripts.data.github.getGitHubPullRequest
import flank.scripts.data.github.getLabelsFromIssue
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.data.github.setAssigneesToPullRequest
import flank.scripts.data.github.setLabelsToPullRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun copyGitHubProperties(githubToken: String, prNumber: Int) = runBlocking {
    getGitHubPullRequest(githubToken, prNumber)
        .onError { println("Could not copy properties, because of ${it.message}") }
        .success { pullRequest ->
            val issueNumber = pullRequest.findReferenceNumber()
            checkNotNull(issueNumber) { "Reference issue not found on description and branch" }
            println("Found referenced issue #$issueNumber")
            launch(Dispatchers.IO) {
                copyGitHubProperties(
                    githubToken,
                    issueNumber,
                    prNumber
                )
            }
        }
}

internal fun GithubPullRequest.findReferenceNumber() =
    (tryGetReferenceNumberFromBody() ?: tryGetReferenceNumberFromBranch())
        ?.trim()
        ?.replace("#", "")
        ?.toInt()

private fun GithubPullRequest.tryGetReferenceNumberFromBody() = bodyReferenceRegex.find(body)?.value

private fun GithubPullRequest.tryGetReferenceNumberFromBranch() = branchReferenceRegex.find(head?.ref.orEmpty())?.value

private val bodyReferenceRegex = "#\\d+\\s".toRegex()
private val branchReferenceRegex = "#\\d+".toRegex()

private suspend fun copyGitHubProperties(
    githubToken: String,
    baseIssueNumber: Int,
    prNumber: Int
) = coroutineScope {
    listOf(
        launch { copyAssignees(githubToken, baseIssueNumber, prNumber) },
        launch { copyLabels(githubToken, baseIssueNumber, prNumber) },
    ).joinAll()
}

internal suspend fun copyAssignees(githubToken: String, baseIssueNumber: Int, pullRequestNumber: Int) {
    getGitHubIssue(githubToken, baseIssueNumber)
        .onError { println("Could not copy assignees because of ${it.message}") }
        .map { githubIssue -> githubIssue.assignees.map { it.login } }
        .getOrNull()
        ?.let { setAssigneesToPullRequest(githubToken, pullRequestNumber, it) }
}

internal suspend fun copyLabels(githubToken: String, issueNumber: Int, pullRequestNumber: Int) {
    getLabelsFromIssue(githubToken, issueNumber)
        .onError { println("Could not copy labels because of ${it.message}") }
        .map { it.map { label -> label.name } }
        .getOrNull()
        ?.run { setLabelsToPullRequest(githubToken, pullRequestNumber, this) }
}
