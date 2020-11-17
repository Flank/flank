package flank.scripts.github

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitResult
import com.jcabi.github.Coordinates
import com.jcabi.github.Release
import com.jcabi.github.Releases
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import flank.scripts.ci.releasenotes.GithubReleaseDeserializable
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toGithubException

// ============= HTTP GITHUB API =============

suspend fun getPrDetailsByCommit(commitSha: String, githubToken: String) =
    Fuel.get("https://api.github.com/repos/flank/flank/commits/$commitSha/pulls")
        .appendHeaders(githubToken)
        .awaitResult(GithubPullRequestListDeserializer)
        .mapClientError { it.toGithubException() }

suspend fun getLatestReleaseTag(githubToken: String) =
    Fuel.get("https://api.github.com/repos/flank/flank/releases/latest")
        .appendHeaders(githubToken)
        .awaitResult(GithubReleaseDeserializable)
        .mapClientError { it.toGithubException() }

fun deleteOldTag(tag: String, username: String, password: String) =
    Fuel.delete(DELETE_ENDPOINT + tag)
        .authentication()
        .basic(username, password)
        .response()
        .third
        .mapClientError { it.toGithubException() }

suspend fun getGitHubPullRequest(githubToken: String, issueNumber: Int) =
    Fuel.get("https://api.github.com/repos/Flank/flank/pulls/$issueNumber")
        .appendHeaders(githubToken)
        .awaitResult(GithubPullRequestDeserializer)
        .mapClientError { it.toGithubException() }

fun Request.appendHeaders(githubToken: String) =
    appendHeader("Accept", "application/vnd.github.v3+json")
        .appendHeader("Authorization", "token $githubToken")

private const val DELETE_ENDPOINT = "https://api.github.com/repos/Flank/flank/git/refs/tags/"

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
