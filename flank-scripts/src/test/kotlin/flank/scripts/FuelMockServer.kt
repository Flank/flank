package flank.scripts

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import flank.scripts.zenhub.ZENHUB_BASE_URL

class FuelMockServer : Client {
    override fun executeRequest(request: Request): Response {
        val url = request.url.toString()
        return when {
            url.startsWith("https://api.github.com/repos/flank/flank/", ignoreCase = true) -> handleGithubMockRequest(url, request)
            url.startsWith(ZENHUB_BASE_URL) -> handleZenhubMockRequest(url, request)
            url == "https://path.com/to/test/settings.gradle.kts" -> request.buildResponse("not empty", 200)
            else -> Response(request.url)
        }
    }
}

fun Request.buildResponse(body: String, statusCode: Int) =
    Response(
        url, statusCode = statusCode, responseMessage = body,
        body = DefaultBody(
            { body.byteInputStream() },
            { body.length.toLong() }
        )
    )
