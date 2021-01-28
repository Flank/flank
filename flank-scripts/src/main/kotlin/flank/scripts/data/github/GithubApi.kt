package flank.scripts.data.github

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.github.kittinunf.fuel.coroutines.awaitStringResult
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.onError
import com.github.kittinunf.result.success
import com.jcabi.github.Coordinates
import com.jcabi.github.Release
import com.jcabi.github.Releases
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import flank.common.config.flankRepository
import flank.scripts.data.github.objects.GitHubCommit
import flank.scripts.data.github.objects.GitHubCommitListDeserializer
import flank.scripts.data.github.objects.GitHubCreateIssueCommentRequest
import flank.scripts.data.github.objects.GitHubCreateIssueCommentResponse
import flank.scripts.data.github.objects.GitHubCreateIssueCommentResponseDeserializer
import flank.scripts.data.github.objects.GitHubCreateIssueRequest
import flank.scripts.data.github.objects.GitHubCreateIssueResponse
import flank.scripts.data.github.objects.GitHubCreateIssueResponseDeserializer
import flank.scripts.data.github.objects.GitHubLabelDeserializable
import flank.scripts.data.github.objects.GitHubRelease
import flank.scripts.data.github.objects.GitHubSetAssigneesRequest
import flank.scripts.data.github.objects.GitHubSetLabelsRequest
import flank.scripts.data.github.objects.GitHubUpdateIssueRequest
import flank.scripts.data.github.objects.GitHubWorkflowRunsSummary
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.data.github.objects.GithubPullRequestDeserializer
import flank.scripts.data.github.objects.GithubPullRequestListDeserializer
import flank.scripts.data.github.objects.GithubReleaseDeserializable
import flank.scripts.data.github.objects.GithubWorkflowRunsSummaryDeserializer
import flank.scripts.utils.exceptions.mapClientErrorToGithubException
import flank.scripts.utils.toJson

private const val URL_BASE = "https://api.github.com/repos"

// ============= HTTP GITHUB API =============
suspend fun getPrDetailsByCommit(commitSha: String, githubToken: String, repo: String = flankRepository): Result<List<GithubPullRequest>, Exception> =
    Fuel.get("$URL_BASE/$repo/commits/$commitSha/pulls")
        .appendGitHubHeaders(githubToken, "application/vnd.github.groot-preview+json")
        .awaitResult(GithubPullRequestListDeserializer)
        .mapClientErrorToGithubException()
        .onError { println("Could not download info for commit $commitSha, because of ${it.message}") }

suspend fun getLatestReleaseTag(githubToken: String, repo: String = flankRepository): Result<GitHubRelease, Exception> =
    Fuel.get("$URL_BASE/$repo/releases/latest")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubReleaseDeserializable)
        .mapClientErrorToGithubException()

suspend fun getGitHubPullRequest(githubToken: String, issueNumber: Int, repo: String = flankRepository): Result<GithubPullRequest, Exception> =
    Fuel.get("$URL_BASE/$repo/pulls/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubIssue(githubToken: String, issueNumber: Int, repo: String = flankRepository): Result<GithubPullRequest, Exception> =
    Fuel.get("$URL_BASE/$repo/issues/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubIssueList(githubToken: String, parameters: Parameters = emptyList(), repo: String = flankRepository): Result<List<GithubPullRequest>, Exception> =
    Fuel.get("$URL_BASE/$repo/issues", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubPullRequestListDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubCommitList(githubToken: String, parameters: Parameters = emptyList(), repo: String = flankRepository): Result<List<GitHubCommit>, Exception> =
    Fuel.get("$URL_BASE/$repo/commits", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GitHubCommitListDeserializer)
        .mapClientErrorToGithubException()

suspend fun getGitHubWorkflowRunsSummary(githubToken: String, workflow: String, parameters: Parameters = emptyList(), repo: String = flankRepository): Result<GitHubWorkflowRunsSummary, Exception> =
    Fuel.get("$URL_BASE/$repo/actions/workflows/$workflow/runs", parameters)
        .appendGitHubHeaders(githubToken)
        .awaitResult(GithubWorkflowRunsSummaryDeserializer)
        .mapClientErrorToGithubException()

suspend fun postNewIssueComment(githubToken: String, issueNumber: Int, payload: GitHubCreateIssueCommentRequest, repo: String = flankRepository): Result<GitHubCreateIssueCommentResponse, Exception> =
    Fuel.post("$URL_BASE/$repo/issues/$issueNumber/comments")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .awaitResult(GitHubCreateIssueCommentResponseDeserializer)
        .mapClientErrorToGithubException()

suspend fun postNewIssue(githubToken: String, payload: GitHubCreateIssueRequest, repo: String = flankRepository): Result<GitHubCreateIssueResponse, Exception> =
    Fuel.post("$URL_BASE/$repo/issues")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .awaitResult(GitHubCreateIssueResponseDeserializer)
        .mapClientErrorToGithubException()

suspend fun getLabelsFromIssue(githubToken: String, issueNumber: Int, repo: String = flankRepository) =
    Fuel.get("$URL_BASE/$repo/issues/$issueNumber/labels")
        .appendGitHubHeaders(githubToken)
        .awaitResult(GitHubLabelDeserializable)
        .mapClientErrorToGithubException()

suspend fun setLabelsToPullRequest(githubToken: String, pullRequestNumber: Int, labels: List<String>, repo: String = flankRepository) {
    Fuel.post("$URL_BASE/$repo/issues/$pullRequestNumber/labels")
        .appendGitHubHeaders(githubToken)
        .body(GitHubSetLabelsRequest(labels).toJson())
        .awaitStringResult()
        .onError { println("Could not set assignees because of ${it.message}") }
        .success { println("$labels set to pull request #$pullRequestNumber") }
}

suspend fun setAssigneesToPullRequest(githubToken: String, pullRequestNumber: Int, assignees: List<String>, repo: String = flankRepository) {
    Fuel.post("$URL_BASE/$repo/issues/$pullRequestNumber/assignees")
        .appendGitHubHeaders(githubToken)
        .body(GitHubSetAssigneesRequest(assignees).toJson())
        .awaitStringResult()
        .onError {
            println("Could not set assignees because of ${it.message}")
            it.printStackTrace()
        }
        .success { println("$assignees set to pull request #$pullRequestNumber") }
}

fun patchIssue(githubToken: String, issueNumber: Int, payload: GitHubUpdateIssueRequest, repo: String = flankRepository): Result<ByteArray, Exception> =
    Fuel.patch("$URL_BASE/$repo/issues/$issueNumber")
        .appendGitHubHeaders(githubToken)
        .body(payload.toJson())
        .response()
        .third
        .mapClientErrorToGithubException()

fun deleteOldTag(tag: String, username: String, password: String, repo: String = flankRepository): Result<ByteArray, Exception> =
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
