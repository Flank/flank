package flank.scripts.ci

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitObject
import com.github.kittinunf.fuel.coroutines.awaitResult
import flank.scripts.ci.releasenotes.GithubPullRequestDeserializer
import flank.scripts.ci.releasenotes.GithubReleaseDeserializable

suspend fun getPrDetailsByCommit(commitSha: String, githubToken: String) =
    Fuel.get("https://api.github.com/repos/flank/flank/commits/$commitSha/pulls")
        .appendHeader("Authorization", "token $githubToken")
        .appendHeader("Accept", "application/vnd.github.groot-preview+json")
        .awaitResult(GithubPullRequestDeserializer)

suspend fun getLatestReleaseTag(githubToken: String) =
    Fuel.get("https://api.github.com/repos/flank/flank/releases/latest")
        .appendHeader("Authorization", "application/vnd.github.v3+json")
        .appendHeader("Authorization", "token $githubToken")
        .awaitObject(GithubReleaseDeserializable)
        .tag
