package flank.scripts

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import flank.scripts.release.updatebugsnag.BugSnagRequest
import flank.scripts.release.updatebugsnag.BugSnagResponse
import flank.scripts.utils.toJson
import flank.scripts.utils.toObject
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.Statement

class FuelTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    override fun withBeforeClasses(statement: Statement?): Statement {
        startMockClient()
        return super.withBeforeClasses(statement)
    }

    private fun startMockClient() {
        FuelManager.instance.client = object : Client {
            override fun executeRequest(request: Request): Response {
                return when (request.url.toString()) {
                    "https://api.github.com/repos/Flank/flank/git/refs/tags/success" -> request.buildResponse("", 200)
                    "https://api.github.com/repos/Flank/flank/git/refs/tags/failure" -> request.buildResponse(GITHUB_ERROR_BODY, 422)
                    "https://build.bugsnag.com/" -> {
                        val body = request.body.asString("application/json").toObject(BugSnagRequest.serializer())
                        if(body.apiKey == "success") {
                            request.buildResponse(body = BugSnagResponse("success")
                                    .toJson(BugSnagResponse.serializer()), statusCode = 200)
                        } else {
                            request.buildResponse(
                                    body = BugSnagResponse(
                                            status = "failure",
                                            errors = listOf("errors")
                                    ).toJson(BugSnagResponse.serializer()), statusCode = 422)
                        }
                    }
                    else -> Response(request.url)
                }
            }
        }
    }

    private fun Request.buildResponse(body: String, statusCode: Int) =
            Response(url, statusCode = statusCode, responseMessage = body, body = DefaultBody(
                    { body.byteInputStream() },
                    { body.length.toLong() }
            ))

}

private val GITHUB_ERROR_BODY = """
            {
              "message": "Bad credentials",
              "documentation_url": "https://developer.github.com/v3"
            }
        """.trimIndent()
