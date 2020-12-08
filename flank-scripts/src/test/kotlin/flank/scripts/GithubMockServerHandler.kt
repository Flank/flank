package flank.scripts

import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Request
import flank.scripts.ci.releasenotes.GitHubRelease
import flank.scripts.github.objects.GitHubCommit
import flank.scripts.github.objects.GitHubCreateIssueCommentResponse
import flank.scripts.github.objects.GitHubCreateIssueResponse
import flank.scripts.github.objects.GitHubLabel
import flank.scripts.github.objects.GitHubWorkflowRunsSummary
import flank.scripts.github.objects.GithubPullRequest
import flank.scripts.github.objects.GithubUser
import flank.scripts.utils.toJson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("ComplexMethod")
fun handleGithubMockRequest(url: String, request: Request) = when {
    request.isFailedGithubRequest() -> request.buildResponse(githubErrorBody, 422)
    url ends """/actions/workflows/[a-zA-Z.-]*/runs""" -> request.buildResponse(workflowSummary, 200)
    url ends """/issues/\d/comments""" && request.method == Method.GET -> request.buildResponse(testGithubIssueCommentList, 200)
    url ends """/issues/\d/comments""" && request.method == Method.POST -> request.buildResponse(createComment, 200)
    url.endsWith("/git/refs/tags/success") -> request.buildResponse("", 200)
    url.endsWith("/releases/latest") && request.containsSuccessHeader() ->
        request.buildResponse(GitHubRelease("v20.08.0").toJson(), 200)
    url.endsWith("/commits/success/pulls") -> request.buildResponse(Json.encodeToString(githubPullRequestTest), 200)
    url.endsWith("/commits") -> request.buildResponse(testGithubIssueList, 200)
    url.endsWith("/issues") && request.method == Method.GET -> request.buildResponse(
        githubPullRequestTest.toJson(),
        200
    )
    url.endsWith("/issues") && request.method == Method.POST -> request.buildResponse(createIssue, 200)
    url.endsWith("/labels") && request.containsSuccessHeader() -> request.buildResponse(testGithubLabels.toJson(), 200)
    (url.contains("/pulls/") || url.contains("/issues/")) && request.containsSuccessHeader() ->
        request.buildResponse(githubPullRequestTest.first().toJson(), 200)
    else -> error("Not supported request")
}

private infix fun String.ends(suffix: String) = suffix.toRegex().containsMatchIn(this)

private fun Request.containsSuccessHeader() = request.headers["Authorization"].contains("token success")

private fun Request.isFailedGithubRequest() =
    url.toString() == "https://api.github.com/repos/Flank/flank/git/refs/tags/failure" ||
        request.headers["Authorization"].contains("token failure")

private val testGithubIssueList = listOf(
    GitHubCommit("123abc"),
    GitHubCommit("456def")
).toJson()

private val testGithubIssueCommentList = listOf(
    GitHubCreateIssueCommentResponse(1, "anyBody", "https://bla.org"),
    GitHubCreateIssueCommentResponse(2, "anyBody", "https://bla.org")
).toJson()

private val workflowSummary = GitHubWorkflowRunsSummary(1, emptyList()).toJson()
private val createIssue = GitHubCreateIssueResponse(1, "https://bla.org", "any body", 123).toJson()
private val createComment = GitHubCreateIssueCommentResponse(2, "https://bla.org", "any body").toJson()

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

