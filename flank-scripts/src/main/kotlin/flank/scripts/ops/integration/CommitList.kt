package flank.scripts.ops.integration

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.scripts.github.getGitHubCommitList
import flank.scripts.github.getPrDetailsByCommit
import flank.scripts.github.objects.GithubPullRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun getCommitListSinceDate(
    token: String,
    since: String
): List<Pair<String, GithubPullRequest?>> = coroutineScope {
    getGitHubCommitList(token, listOf("since" to since))
        .onError { println(it.message) }
        .getOrElse { emptyList() }
        .map {
            async {
                it.sha to getPrDetailsByCommit(it.sha, token).getOrElse { emptyList() }
            }
        }
        .awaitAll()
        .flatMap { (commit, prs) ->
            if (prs.isEmpty()) listOf(commit to null)
            else prs.map { commit to it }
        }
        .toList()
}
