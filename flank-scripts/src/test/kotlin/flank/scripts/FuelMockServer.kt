package flank.scripts

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import flank.scripts.release.updatebugsnag.BugSnagRequest
import flank.scripts.release.updatebugsnag.BugSnagResponse
import flank.scripts.utils.toJson
import flank.scripts.utils.toObject
import flank.scripts.zenhub.ZENHUB_BASE_URL

class FuelMockServer : Client {
    override fun executeRequest(request: Request): Response {
        val url = request.url.toString()
        return when {
            url.startsWith("https://api.github.com/repos/flank/flank/", ignoreCase = true) -> handleGithubMockRequest(url, request)
            url.startsWith(ZENHUB_BASE_URL) -> handleZenhubMockRequest(url, request)
            url == "https://build.bugsnag.com/" -> request.handleBugsnagResponse()
            else -> Response(request.url)
        }
    }
}

private fun Request.handleBugsnagResponse() =
    if (body.asString("application/json").toObject<BugSnagRequest>().apiKey == "success") {
        buildResponse(
            body = BugSnagResponse("success").toJson(),
            statusCode = 200
        )
    } else {
        buildResponse(
            body = BugSnagResponse(
                status = "failure",
                errors = listOf("errors")
            ).toJson(),
            statusCode = 422
        )
    }

fun Request.buildResponse(body: String, statusCode: Int) =
    Response(
        url, statusCode = statusCode, responseMessage = body,
        body = DefaultBody(
            { body.byteInputStream() },
            { body.length.toLong() }
        )
    )
