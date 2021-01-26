package flank.scripts.ops.integration

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.common.config.integrationOpenedIssueUser
import flank.scripts.github.getGitHubIssueList

internal suspend fun checkForOpenedITIssues(token: String): Int? = getGitHubIssueList(
    githubToken = token,
    parameters = listOf(
        "creator" to integrationOpenedIssueUser,
        "state" to "open",
        "labels" to "IT_Failed"
    )
)
    .onError { println(it.message) }
    .getOrElse { emptyList() }
    .firstOrNull()
    .also {
        if (it != null) println("** Issue found: ${it.htmlUrl}")
        else println("** No opened issue")
    }?.number
