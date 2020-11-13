package flank.scripts

import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import flank.scripts.pullrequest.ZenHubEstimate
import flank.scripts.pullrequest.ZenHubIssue
import flank.scripts.utils.toJson

fun handleZenhubMockRequest(url: String, request: Request) = when {
    url.contains("issues") && request.method == Method.GET && request.isSuccessful() ->
        request.buildResponse(testZenHubIssue.toJson(), 200)
    url.contains("issues") && request.method == Method.PUT && request.isSuccessful() ->
        request.buildResponse("", 201)

    else -> request.buildResponse("Bad authentication", 401)
}

val testZenHubIssue = ZenHubIssue(ZenHubEstimate(3))

private fun Request.isSuccessful() = request.headers["X-Authentication-Token"].contains("success")
