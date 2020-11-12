package flank.scripts.pullrequest

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import flank.scripts.utils.toJson
import kotlinx.serialization.Serializable

private const val FLANK_REPO_ID = 84221974
private const val ZENHUB_BASE_URL = "https://api.zenhub.com/p1/repositories/$FLANK_REPO_ID"

suspend fun copyEstimations(zenhubToken: String, issueNumber: Int, pullRequestNumber: Int) {
    getEstimation(zenhubToken, issueNumber)
        ?.run { setEstimation(zenhubToken, pullRequestNumber, estimate.value) }
}

suspend fun getEstimation(zenhubToken: String, issueNumber: Int) =
    Fuel.get("$ZENHUB_BASE_URL/issues/$issueNumber")
        .withZenhubHeaders(zenhubToken)
        .awaitResult(ZenHubIssueDeserializable)
        .onError { println("Could not get estimations because of ${it.message}") }
        .getOrNull()

private suspend fun setEstimation(zenhubToken: String, pullRequestNumber: Int, estimate: Int) {
    Fuel.put("$ZENHUB_BASE_URL/issues/$pullRequestNumber/estimate")
        .withZenhubHeaders(zenhubToken)
        .body(ZenHubEstimateRequest(estimate).toJson())
        .awaitStringResult()
        .onError { println("Could not set estimations because of ${it.message}") }
        .success { println("Estimate $estimate set to pull request #$pullRequestNumber") }
}

private fun Request.withZenhubHeaders(zenhubToken: String) =
    appendHeader("Content-Type", "application/json")
        .appendHeader("X-Authentication-Token", zenhubToken)

@Serializable
private data class ZenHubEstimateRequest(val estimate: Int)
