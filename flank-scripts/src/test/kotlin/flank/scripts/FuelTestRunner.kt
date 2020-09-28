package flank.scripts

import com.github.kittinunf.fuel.core.Client
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.requests.DefaultBody
import flank.scripts.ci.releasenotes.GitHubRelease
import flank.scripts.ci.releasenotes.GithubPullRequest
import flank.scripts.release.updatebugsnag.BugSnagRequest
import flank.scripts.release.updatebugsnag.BugSnagResponse
import flank.scripts.utils.toJson
import flank.scripts.utils.toObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
                val url = request.url.toString()
                return when {
                    url == "https://api.github.com/repos/Flank/flank/git/refs/tags/success" -> request.buildResponse("", 200)
                    url == "https://api.github.com/repos/flank/flank/releases/latest" && request.headers["Authorization"].contains("token success") -> request.buildResponse(GitHubRelease("v20.08.0").toJson(), 200)
                    url == "https://api.github.com/repos/flank/flank/commits/success/pulls" -> request.buildResponse(
                        Json.encodeToString(githubPullRequestTest),
                        200
                    )
                    request.isFailedGithubRequest() -> request.buildResponse(githubErrorBody, 422)
                    url == "https://build.bugsnag.com/" -> request.handleBugsnagResponse()
                    else -> Response(request.url)
                }
            }
        }
    }

    private fun Request.isFailedGithubRequest() =
        url.toString() == "https://api.github.com/repos/Flank/flank/git/refs/tags/failure" ||
            (url.toString()
                .startsWith("https://api.github.com/") && request.headers["Authorization"].contains("token failure"))

    private fun Request.buildResponse(body: String, statusCode: Int) =
        Response(
            url,
            statusCode = statusCode,
            responseMessage = body,
            body = DefaultBody(
                { body.byteInputStream() },
                { body.length.toLong() }
            )
        )

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
}

private val githubPullRequestTest = listOf(
    GithubPullRequest(
        "www.pull.request",
        "feat: new Feature",
        5,
        listOf()
    )
)

private val githubErrorBody = """
            {
              "message": "Bad credentials",
              "documentation_url": "https://developer.github.com/v3"
            }
        """.trimIndent()
