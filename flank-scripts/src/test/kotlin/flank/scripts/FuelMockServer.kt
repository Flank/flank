package flank.scripts

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import flank.scripts.ops.firebase.testContent

class FuelMockServer : Client {
    override fun executeRequest(request: Request): Response {
        val url = request.url.toString()
        return when {
            url.startsWith("https://api.github.com/repos/flank/flank/", ignoreCase = true) -> handleGithubMockRequest(url, request)
            url == "http://test.account.service" -> request.buildResponse(testContent, 200)
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
