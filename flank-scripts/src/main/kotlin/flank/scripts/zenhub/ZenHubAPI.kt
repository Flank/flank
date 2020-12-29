package flank.scripts.zenhub

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.utils.toJson
import flank.scripts.zenhub.objects.ConvertToEpicRequest
import flank.scripts.zenhub.objects.UpdateEpicRequest
import kotlinx.serialization.Serializable

const val FLANK_REPO_ID = 84221974
internal const val ZENHUB_BASE_URL = "https://api.zenhub.com/p1/repositories/$FLANK_REPO_ID"

// GET
suspend fun copyEstimation(zenhubToken: String, issueNumber: Int, pullRequestNumber: Int) {
    getEstimation(zenhubToken, issueNumber)
        ?.run { setEstimation(zenhubToken, pullRequestNumber, estimate.value) }
        ?: println("Could not copy estimation because it is not provided")
}

private suspend fun getEstimation(zenhubToken: String, issueNumber: Int) =
    Fuel.get("$ZENHUB_BASE_URL/issues/$issueNumber")
        .withZenhubHeaders(zenhubToken)
        .awaitResult(ZenHubIssueDeserializable)
        .onError { println("Could not get estimations because of ${it.message}") }
        .getOrNull()

// PUT
private suspend fun setEstimation(zenhubToken: String, pullRequestNumber: Int, estimate: Int) {
    Fuel.put("$ZENHUB_BASE_URL/issues/$pullRequestNumber/estimate")
        .withZenhubHeaders(zenhubToken)
        .body(ZenHubEstimateRequest(estimate).toJson())
        .awaitStringResult()
        .onError { println("Could not set estimations because of ${it.message}") }
        .success { println("Estimate $estimate set to pull request #$pullRequestNumber") }
}

// POST
fun convertIssueToEpic(zenhubToken: String, issueNumber: Int, payload: ConvertToEpicRequest) =
    Fuel.post("$ZENHUB_BASE_URL/issues/$issueNumber/convert_to_epic")
        .withZenhubHeaders(zenhubToken)
        .body(payload.toJson())
        .response()
        .third
        .onError { println("Unable to convert to epic: ${it.message}") }
        .success { println("** Issue $issueNumber successfully converted to an epic") }

fun updateEpic(zenhubToken: String, issueNumber: Int, payload: UpdateEpicRequest) =
    Fuel.post("$ZENHUB_BASE_URL/epics/$issueNumber/update_issues")
        .withZenhubHeaders(zenhubToken)
        .body(payload.toJson())
        .response()
        .third
        .onError { println("Unable to update epic: ${it.message}") }
        .success { println("** Issues successfully added to the issue $issueNumber") }

private fun Request.withZenhubHeaders(zenhubToken: String) =
    appendHeader("Content-Type", "application/json")
        .appendHeader("X-Authentication-Token", zenhubToken)

@Serializable
private data class ZenHubEstimateRequest(val estimate: Int)
