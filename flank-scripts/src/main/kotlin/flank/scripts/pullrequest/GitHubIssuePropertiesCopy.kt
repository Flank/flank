package flank.scripts.pullrequest

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.github.GithubPullRequest
import flank.scripts.github.getGitHubPullRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

object CopyProperties :
    CliktCommand(name = "copyProperties", help = "Copy properties from referenced issue to pull request") {

    private val githubToken by option(help = "Git Token").required()
    private val zenhubToken by option(help = "ZenHub api Token").required()
    private val prNumber by option(help = "Pull request number").int().required()

    override fun run() {
        runBlocking {
            getGitHubPullRequest(githubToken, prNumber)
                .onError { println("Could not copy properties, because of ${it.message}") }
                .success { pullRequest ->
                    val issueNumber = pullRequest.findReferenceNumber()
                    checkNotNull(issueNumber) { "Reference issue not found on description and branch" }
                    println("Found referenced issue #$issueNumber")
                    launch(Dispatchers.IO) { pullRequest.copyGitHubProperties(githubToken, issueNumber, prNumber) }
                    launch(Dispatchers.IO) { copyZenhubProperties(zenhubToken, issueNumber, prNumber) }
                }
        }
    }
}

private suspend fun GithubPullRequest.copyGitHubProperties(
    githubToken: String,
    baseIssueNumber: Int,
    prNumber: Int
) = coroutineScope {
    val assignees = this@copyGitHubProperties.assignees.map { it.login }
    listOf(
        launch { setAssigneesToPullRequest(githubToken, prNumber, assignees) },
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
