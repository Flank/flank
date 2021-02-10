package flank.scripts.ops.github

import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import flank.scripts.data.github.getLabelsFromIssue
import flank.scripts.data.github.setLabelsToPullRequest

internal suspend fun copyLabels(githubToken: String, issueNumber: Int, pullRequestNumber: Int) {
    getLabelsFromIssue(githubToken, issueNumber)
        .onError { println("Could not copy labels because of ${it.message}") }
        .map { it.map { label -> label.name } }
        .getOrNull()
        ?.run { setLabelsToPullRequest(githubToken, pullRequestNumber, this) }
}
