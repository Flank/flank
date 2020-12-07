package flank.scripts.integration

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.scripts.github.getGitHubIssueList

suspend fun checkForOpenedITIssues(token: String) = getGitHubIssueList(
    githubToken = token,
    parameters = listOf(
//        "creator" to "github-actions[bot]",
        "creator" to "pawelpasterz",
        "state" to "open",
        "labels" to "IT_Failed"
    )
)
    .onError { println(it.message) }
    .getOrElse { emptyList() }
    .getOrNull(0)
    .also {
        if (it != null) println("** Issue found: ${it.htmlUrl}")
        else println("** No opened issue")
    }
