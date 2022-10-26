package flank.scripts.ops.firebase

import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.utils.Version

data class SDKUpdateContext(
    val newVersion: Version,
    val oldVersion: Version,
    val githubToken: String,
    val openedIssue: GithubPullRequest?,
    val updatesLazy: suspend () -> String,
)
