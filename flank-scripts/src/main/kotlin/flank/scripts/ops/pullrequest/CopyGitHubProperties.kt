package flank.scripts.ops.pullrequest

import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.github.getGitHubPullRequest
import flank.scripts.zenhub.copyEstimation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun copyGitHubProperties(githubToken: String, zenhubToken: String, prNumber: Int) = runBlocking {
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
            launch(Dispatchers.IO) {
                copyZenhubProperties(
                    zenhubToken,
                    issueNumber,
                    prNumber
                )
            }
        }
}

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

private suspend fun copyZenhubProperties(
    zenhubToken: String,
    baseIssueNumber: Int,
    prNumber: Int
) {
    copyEstimation(zenhubToken, baseIssueNumber, prNumber)
}
