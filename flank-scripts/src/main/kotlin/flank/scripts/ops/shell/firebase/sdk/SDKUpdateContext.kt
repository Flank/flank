package flank.scripts.ops.shell.firebase.sdk

import flank.scripts.github.objects.GithubPullRequest
import flank.scripts.utils.Version

data class SDKUpdateContext(
    val newVersion: Version,
    val oldVersion: Version,
    val githubToken: String,
    val zenhubToken: String,
    val openedIssue: GithubPullRequest?,
    val updatesLazy: suspend () -> String,
)
