package flank.scripts.ops.github

import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import flank.scripts.data.github.getGitHubIssue
import flank.scripts.data.github.setAssigneesToPullRequest

internal suspend fun copyAssignees(githubToken: String, baseIssueNumber: Int, pullRequestNumber: Int) {
    getGitHubIssue(githubToken, baseIssueNumber)
        .onError { println("Could not copy assignees because of ${it.message}") }
        .map { githubIssue -> githubIssue.assignees.map { it.login } }
        .getOrNull()
        ?.let { setAssigneesToPullRequest(githubToken, pullRequestNumber, it) }
}
