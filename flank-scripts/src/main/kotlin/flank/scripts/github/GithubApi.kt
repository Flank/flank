package flank.scripts.github

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitResult
import flank.scripts.ci.releasenotes.GithubPullRequestDeserializer
import flank.scripts.ci.releasenotes.GithubReleaseDeserializable
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toGithubException

suspend fun getPrDetailsByCommit(commitSha: String, githubToken: String) =
    Fuel.get("https://api.github.com/repos/flank/flank/commits/$commitSha/pulls")
        .appendHeader("Accept", "application/vnd.github.groot-preview+json")
        .appendHeader("Authorization", "token $githubToken")
        .awaitResult(GithubPullRequestDeserializer)
        .mapClientError { it.toGithubException() }

suspend fun getLatestReleaseTag(githubToken: String) =
    Fuel.get("https://api.github.com/repos/flank/flank/releases/latest")
        .appendHeader("Accept", "application/vnd.github.v3+json")
        .appendHeader("Authorization", "token $githubToken")
        .awaitResult(GithubReleaseDeserializable)
        .mapClientError { it.toGithubException() }

fun deleteOldTag(tag: String, username: String, password: String) =
    Fuel.delete(DELETE_ENDPOINT + tag)
        .authentication()
        .basic(username, password)
        .response()
        .third
        .mapClientError { it.toGithubException() }

private const val DELETE_ENDPOINT = "https://api.github.com/repos/Flank/flank/git/refs/tags/"
