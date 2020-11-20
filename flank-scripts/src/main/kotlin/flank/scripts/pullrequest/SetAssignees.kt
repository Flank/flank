package flank.scripts.pullrequest

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.github.appendGitHubHeaders
import flank.scripts.github.getGitHubIssue
import flank.scripts.utils.toJson
import kotlinx.serialization.Serializable

suspend fun copyAssignees(githubToken: String, baseIssueNumber: Int, pullRequestNumber: Int) {
    getGitHubIssue(githubToken, baseIssueNumber)
        .onError { println("Could not copy assignees because of ${it.message}") }
        .map { githubIssue -> githubIssue.assignees.map { it.login } }
        .getOrNull()
        ?.let { setAssigneesToPullRequest(githubToken, pullRequestNumber, it) }
}

private suspend fun setAssigneesToPullRequest(githubToken: String, pullRequestNumber: Int, assignees: List<String>) {
    Fuel.post("https://api.github.com/repos/Flank/flank/issues/$pullRequestNumber/assignees")
        .appendGitHubHeaders(githubToken)
        .body(SetAssigneesRequest(assignees).toJson())
        .awaitStringResult()
        .onError {
            println("Could not set assignees because of ${it.message}")
            it.printStackTrace()
        }
        .success { println("$assignees set to pull request #$pullRequestNumber") }
}

@Serializable
private data class SetAssigneesRequest(
    val assignees: List<String>
)
