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
import flank.scripts.exceptions.mapClientErrorToGithubException
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

private const val FLANK_REPO = "Flank/flank"
private const val URL_BASE = "https://api.github.com/repos"

// ============= HTTP GITHUB API =============
// GET
suspend fun getPrDetailsByCommit(commitSha: String, githubToken: String, repo: String = FLANK_REPO): Result<List<GithubPullRequest>, Exception> =
    Fuel.get("$URL_BASE/$repo/commits/$commitSha/pulls")
        .appendGitHubHeaders(githubToken, "application/vnd.github.groot-preview+json")
        .awaitResult(GithubPullRequestListDeserializer)
        .mapClientErrorToGithubException()
        .onError { println("Could not download info for commit $commitSha, because of ${it.message}") }

suspend fun getLatestReleaseTag(githubToken: String, repo: String = FLANK_REPO): Result<GitHubRelease, Exception> =
    Fuel.get("$URL_BASE/$repo/releases/latest")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubReleaseDeserializable)
        .mapClientErrorToGithubException()

suspend fun getGitHubPullRequest(githubToken: String, issueNumber: Int, repo: String = FLANK_REPO): Result<GithubPullRequest, Exception> =
    Fuel.get("$URL_BASE/$repo/pulls/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubIssue(githubToken: String, issueNumber: Int, repo: String = FLANK_REPO): Result<GithubPullRequest, Exception> =
    Fuel.get("$URL_BASE/$repo/issues/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubIssueList(githubToken: String, parameters: Parameters = emptyList(), repo: String = FLANK_REPO): Result<List<GithubPullRequest>, Exception> =
    Fuel.get("$URL_BASE/$repo/issues", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestListDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubCommitList(githubToken: String, parameters: Parameters = emptyList(), repo: String = FLANK_REPO): Result<List<GitHubCommit>, Exception> =
    Fuel.get("$URL_BASE/$repo/commits", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GitHubCommitListDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubWorkflowRunsSummary(githubToken: String, workflow: String, parameters: Parameters = emptyList(), repo: String = FLANK_REPO): Result<GitHubWorkflowRunsSummary, Exception> =
    Fuel.get("$URL_BASE/$repo/actions/workflows/$workflow/runs", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubWorkflowRunsSummaryDeserializer)
        .mapClientErrorToGithubException()

// POST
suspend fun postNewIssueComment(githubToken: String, issueNumber: Int, payload: GitHubCreateIssueCommentRequest, repo: String = FLANK_REPO): Result<GitHubCreateIssueCommentResponse, Exception> =
    Fuel.post("$URL_BASE/$repo/issues/$issueNumber/comments")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .awaitResult(GitHubCreateIssueCommentResponseDeserializer)
        .mapClientErrorToGithubException()

suspend fun postNewIssue(githubToken: String, payload: GitHubCreateIssueRequest, repo: String = FLANK_REPO): Result<GitHubCreateIssueResponse, Exception> =
    Fuel.post("$URL_BASE/$repo/issues")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .awaitResult(GitHubCreateIssueResponseDeserializer)
        .mapClientErrorToGithubException()

// PATCH
fun patchIssue(githubToken: String, issueNumber: Int, payload: GitHubUpdateIssueRequest, repo: String = FLANK_REPO): Result<ByteArray, Exception> =
    Fuel.patch("$URL_BASE/$repo/issues/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .response()
        .third
        .mapClientErrorToGithubException()

// DELETE
fun deleteOldTag(tag: String, username: String, password: String, repo: String = FLANK_REPO): Result<ByteArray, Exception> =
    Fuel.delete("$URL_BASE/$repo/git/refs/tags/$tag")
        .authentication()
        .basic(username, password)
        .response()
        .third
        .mapClientErrorToGithubException()

fun Request.appendGitHubHeaders(githubToken: String, contentType: String = "application/vnd.github.v3+json") =
    appendHeader("Accept", contentType)
        .appendHeader("Authorization", "token $githubToken")

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
