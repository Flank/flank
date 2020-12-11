@file:Suppress("EXPERIMENTAL_API_USAGE")

package flank.scripts.integration

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.scripts.github.getGitHubCommitList
import flank.scripts.github.getPrDetailsByCommit
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

suspend fun getCommitListSinceDate(token: String, since: String) = coroutineScope {
    getGitHubCommitList(token, listOf("since" to since))
        .onError { println(it.message) }
        .getOrElse { emptyList() }
        .asFlow()
        .flatMapMerge {
            channelFlow {
                launch {
                    send(it.sha to getPrDetailsByCommit(it.sha, token).getOrElse { emptyList() }) }
            }
        }
        .flatMapMerge { (commit, prs) ->
            flow {
                if (prs.isEmpty()) emit(commit to null)
                else prs.forEach { emit(commit to it) }
            }
        }
        .toList()
}
