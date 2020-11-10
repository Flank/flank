package flank.scripts.pullrequest

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitResult
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toGithubException

suspend fun getGitHubPullRequest(githubToken: String, issueNumber: Int) =
    Fuel.get("https://api.github.com/repos/Flank/flank/pulls/$issueNumber")
        .appendHeader("Accept", "application/vnd.github.v3+json")
        .appendHeader("Authorization", "token $githubToken")
        .awaitResult(GitHubPullRequestDeserializable)
        .mapClientError { it.toGithubException() }
