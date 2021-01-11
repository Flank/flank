package flank.scripts.pullrequest

import com.github.kittinunf.result.getOrNull
import com.github.kittinunf.result.map
import com.github.kittinunf.result.onError
import flank.scripts.github.getGitHubIssue
import flank.scripts.github.setAssigneesToPullRequest

suspend fun copyAssignees(githubToken: String, baseIssueNumber: Int, pullRequestNumber: Int) {
    getGitHubIssue(githubToken, baseIssueNumber)
        .onError { println("Could not copy assignees because of ${it.message}") }
        .map { githubIssue -> githubIssue.assignees.map { it.login } }
        .getOrNull()
        ?.let { setAssigneesToPullRequest(githubToken, pullRequestNumber, it) }
}
