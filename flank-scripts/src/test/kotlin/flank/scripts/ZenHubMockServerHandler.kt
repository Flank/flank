package flank.scripts

import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import flank.scripts.utils.toJson
import flank.scripts.zenhub.ZenHubEstimate
import flank.scripts.zenhub.ZenHubIssue

fun handleZenhubMockRequest(url: String, request: Request) = when {
    url.contains("issues") && request.isGet && request.isSuccessful() ->
        request.buildResponse(testZenHubIssue.toJson(), 200)

    url.contains("issues") && request.isPut && request.isSuccessful() ->
        request.buildResponse("", 201)

    url.contains("issues") && request.isPost && request.isSuccessful() ->
        request.buildResponse("", 201)

    else -> request.buildResponse("Bad authentication", 401)
}

val testZenHubIssue = ZenHubIssue(ZenHubEstimate(3))

private val Request.isGet: Boolean
    get() = method == Method.GET

private val Request.isPut: Boolean
    get() = method == Method.PUT

private val Request.isPost: Boolean
    get() = method == Method.POST

private fun Request.isSuccessful() = request.headers["X-Authentication-Token"].contains("success")
