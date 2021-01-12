package flank.scripts.pullrequest

import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import flank.scripts.github.getLabelsFromIssue
import flank.scripts.github.setLabelsToPullRequest

suspend fun copyLabels(githubToken: String, issueNumber: Int, pullRequestNumber: Int) {
    getLabelsFromIssue(githubToken, issueNumber)
        .onError { println("Could not copy labels because of ${it.message}") }
        .map { it.map { label -> label.name } }
        .getOrNull()
        ?.run { setLabelsToPullRequest(githubToken, pullRequestNumber, this) }
}
