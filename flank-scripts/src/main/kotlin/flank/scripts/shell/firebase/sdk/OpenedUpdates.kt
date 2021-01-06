package flank.scripts.shell.firebase.sdk

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.common.config.updatesOpenedUser
import flank.scripts.github.getGitHubIssueList

suspend fun checkForOpenedUpdates(token: String) = getGitHubIssueList(
    githubToken = token,
    parameters = listOf(
        "creator" to updatesOpenedUser,
        "state" to "open",
        "labels" to "gcloud SDK"
    )
)
    .onError { println(it.message) }
    .getOrElse { emptyList() }
    .firstOrNull()
    .also {
        if (it != null) println("** Issue found: ${it.htmlUrl}")
        else println("** No opened issue")
    }
