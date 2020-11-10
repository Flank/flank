package flank.scripts.pullrequest

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.utils.toJson
import kotlinx.serialization.Serializable

suspend fun setAssigneesToPullRequest(githubToken: String, pullRequestNumber: Int, assignees: List<String>) {
    Fuel.post("https://api.github.com/repos/Flank/flank/issues/$pullRequestNumber/assignees")
        .appendHeader("Accept", "application/vnd.github.v3+json")
        .appendHeader("Authorization", "token $githubToken")
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
