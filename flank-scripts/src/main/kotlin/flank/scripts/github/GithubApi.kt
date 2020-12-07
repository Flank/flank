package flank.scripts.github

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onError
import com.jcabi.github.Coordinates
import com.jcabi.github.Release
import com.jcabi.github.Releases
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import flank.scripts.ci.releasenotes.GitHubRelease
import flank.scripts.ci.releasenotes.GithubReleaseDeserializable
import flank.scripts.exceptions.mapErrorToGithubException
import flank.scripts.github.objects.GitHubCommit
import flank.scripts.github.objects.GitHubCommitListDeserializer
import flank.scripts.github.objects.GitHubCreateIssueCommentRequest
import flank.scripts.github.objects.GitHubCreateIssueCommentResponse
import flank.scripts.github.objects.GitHubCreateIssueCommentResponseDeserializer
import flank.scripts.github.objects.GitHubCreateIssueRequest
import flank.scripts.github.objects.GitHubCreateIssueResponse
import flank.scripts.github.objects.GitHubCreateIssueResponseDeserializer
import flank.scripts.github.objects.GitHubUpdateIssueRequest
import flank.scripts.github.objects.GitHubWorkflowRunsSummary
import flank.scripts.github.objects.GithubPullRequest
import flank.scripts.github.objects.GithubPullRequestDeserializer
import flank.scripts.github.objects.GithubPullRequestListDeserializer
import flank.scripts.github.objects.GithubWorkflowRunsSummaryDeserializer
import flank.scripts.utils.toJson
import java.lang.Exception

private const val URL_BASE = "https://api.github.com/repos/Flank/flank"

// ============= HTTP GITHUB API =============
// GET
suspend fun getPrDetailsByCommit(commitSha: String, githubToken: String): Result<List<GithubPullRequest>, Exception> =
    Fuel.get("$URL_BASE/commits/$commitSha/pulls")
        .appendGitHubHeaders(githubToken, "application/vnd.github.groot-preview+json")
        .awaitResult(GithubPullRequestListDeserializer)
        .mapErrorToGithubException()
        .onError { println("Could not download info for commit $commitSha, because of ${it.message}") }

suspend fun getLatestReleaseTag(githubToken: String): Result<GitHubRelease, Exception> =
    Fuel.get("$URL_BASE/releases/latest")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubReleaseDeserializable)
        .mapErrorToGithubException()

suspend fun getGitHubPullRequest(githubToken: String, issueNumber: Int): Result<GithubPullRequest, Exception> =
    Fuel.get("$URL_BASE/pulls/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapErrorToGithubException()

suspend fun getGitHubIssue(githubToken: String, issueNumber: Int): Result<GithubPullRequest, Exception> =
    Fuel.get("$URL_BASE/issues/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapErrorToGithubException()

suspend fun getGitHubIssueList(githubToken: String, parameters: Parameters = emptyList()): Result<List<GithubPullRequest>, Exception> =
    Fuel.get("$URL_BASE/issues", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestListDeserializer)
        .mapErrorToGithubException()

suspend fun getGitHubCommitList(githubToken: String, parameters: Parameters = emptyList()): Result<List<GitHubCommit>, Exception> =
    Fuel.get("$URL_BASE/commits", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GitHubCommitListDeserializer)
        .mapErrorToGithubException()

suspend fun getGitHubWorkflowRunsSummary(githubToken: String, workflow: String, parameters: Parameters = emptyList()): Result<GitHubWorkflowRunsSummary, Exception> =
    Fuel.get("$URL_BASE/actions/workflows/$workflow/runs", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubWorkflowRunsSummaryDeserializer)
        .mapErrorToGithubException()

// POST
suspend fun postNewIssueComment(githubToken: String, issueNumber: Int, payload: GitHubCreateIssueCommentRequest): Result<GitHubCreateIssueCommentResponse, Exception> =
    Fuel.post("$URL_BASE/issues/$issueNumber/comments")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .awaitResult(GitHubCreateIssueCommentResponseDeserializer)
        .mapErrorToGithubException()

suspend fun postNewIssue(githubToken: String, payload: GitHubCreateIssueRequest): Result<GitHubCreateIssueResponse, Exception> =
    Fuel.post("$URL_BASE/issues")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .awaitResult(GitHubCreateIssueResponseDeserializer)
        .mapErrorToGithubException()

// PATCH
fun patchIssue(githubToken: String, issueNumber: Int, payload: GitHubUpdateIssueRequest): Result<ByteArray, Exception> =
    Fuel.patch("$URL_BASE/issues/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .response()
        .third
        .mapErrorToGithubException()

// DELETE
fun deleteOldTag(tag: String, username: String, password: String): Result<ByteArray, Exception> =
    Fuel.delete(DELETE_ENDPOINT + tag)
        .authentication()
        .basic(username, password)
        .response()
        .third
        .mapErrorToGithubException()

fun Request.appendGitHubHeaders(githubToken: String, contentType: String = "application/vnd.github.v3+json") =
    appendHeader("Accept", contentType)
        .appendHeader("Authorization", "token $githubToken")

private const val DELETE_ENDPOINT = "$URL_BASE/git/refs/tags/"

// ============= JCABI GITHUB API =============

fun githubRepo(
    token: String,
    repoPath: String
): Repo =
    RtGithub(token)
        .repos()
        .get(Coordinates.Simple(repoPath))

fun Repo.getRelease(tag: String): Release.Smart? =
    Releases.Smart(releases()).run {
        if (!exists(tag)) null
        else Release.Smart(find(tag))
    }

fun Repo.getOrCreateRelease(tag: String): Release.Smart = releases().getOrCreateRelease(tag)

private fun Releases.getOrCreateRelease(tag: String) =
    Release.Smart(
        try {
            print("* Fetching release $tag - ")
            Releases.Smart(this).find(tag).also { println("OK") }
        } catch (e: IllegalArgumentException) {
            println("FAILED")
            print("* Creating new release $tag - ")
            create(tag).also { println("OK") }
        }
    )
