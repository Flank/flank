package flank.scripts.pullrequest

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toGithubException
import flank.scripts.github.GitHubLabelDeserializable
import flank.scripts.github.appendHeaders
import flank.scripts.utils.toJson
import kotlinx.serialization.Serializable

suspend fun copyLabels(githubToken: String, issueNumber: Int, pullRequestNumber: Int) {
    getLabelsFromIssue(githubToken, issueNumber)
        .onError { println("Could not copy labels because of ${it.message}") }
        .map { it.map { label -> label.name } }
        .getOrNull()
        ?.run { setLabelsToPullRequest(githubToken, pullRequestNumber, this) }
}

private suspend fun getLabelsFromIssue(githubToken: String, issueNumber: Int) =
    Fuel.get("https://api.github.com/repos/Flank/flank/issues/$issueNumber/labels")
        .appendHeaders(githubToken)
        .awaitResult(GitHubLabelDeserializable)
        .mapClientError { it.toGithubException() }

private suspend fun setLabelsToPullRequest(githubToken: String, pullRequestNumber: Int, labels: List<String>) {
    Fuel.post("https://api.github.com/repos/Flank/flank/issues/$pullRequestNumber/labels")
        .appendHeaders(githubToken)
        .body(SetLabelsRequest(labels).toJson())
        .awaitStringResult()
        .onError { println("Could not set assignees because of ${it.message}") }
        .success { println("$labels set to pull request #$pullRequestNumber") }
}

@Serializable
private data class SetLabelsRequest(
    val labels: List<String>
)
