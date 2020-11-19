package flank.scripts

import com.github.kittinunf.fuel.core.Request
import flank.scripts.ci.releasenotes.GitHubRelease
import flank.scripts.github.GitHubLabel
import flank.scripts.github.GithubPullRequest
import flank.scripts.github.GithubUser
import flank.scripts.utils.toJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun handleGithubMockRequest(url: String, request: Request) = when {
    url.endsWith("/git/refs/tags/success") -> request.buildResponse("", 200)
    url.endsWith("/releases/latest") && request.containsSuccessHeader() -> request.buildResponse(
        GitHubRelease("v20.08.0").toJson(),
        200
    )
    url.endsWith("/commits/success/pulls") -> request.buildResponse(Json.encodeToString(githubPullRequestTest), 200)
    url.endsWith("/labels") && request.containsSuccessHeader() -> request.buildResponse(
        testGithubLabels.toJson(), 200
    )
    (url.contains("/pulls/") || url.contains("/issues/")) && request.containsSuccessHeader() -> request.buildResponse(
        githubPullRequestTest.first().toJson(), 200
    )
    request.isFailedGithubRequest() -> request.buildResponse(githubErrorBody, 422)
    else -> error("Not supported request")
}

private fun Request.containsSuccessHeader() = request.headers["Authorization"].contains("token success")

private fun Request.isFailedGithubRequest() =
    url.toString() == "https://api.github.com/repos/Flank/flank/git/refs/tags/failure" ||
        request.headers["Authorization"].contains("token failure")

val testGithubLabels = listOf(
    GitHubLabel("label"),
    GitHubLabel("label2"),
    GitHubLabel("label3"),
)

val testAssignees = listOf(
    GithubUser("test", "www.test.com"),
    GithubUser("test2", "www.test2.com"),
)

private val githubPullRequestTest = listOf(
    GithubPullRequest(
        "www.pull.request",
        "feat: new Feature",
        5,
        testAssignees
    )
)

private val githubErrorBody = """
            {
              "message": "Bad credentials",
              "documentation_url": "https://developer.github.com/v3"
            }
        """.trimIndent()

