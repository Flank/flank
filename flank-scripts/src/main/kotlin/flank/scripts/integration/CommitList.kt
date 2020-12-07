package flank.scripts.integration

import com.github.kittinunf.result.getOrElse
import com.github.kittinunf.result.onError
import flank.scripts.github.getGitHubCommitList
import flank.scripts.github.getPrDetailsByCommit
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

@FlowPreview
suspend fun getCommitListSinceDate(token: String, since: String) = coroutineScope {
    getGitHubCommitList(token, listOf("since" to since))
        .onError { println(it.message) }
        .getOrElse { emptyList() }
        .asFlow()
        .flatMapMerge {
            flow {
                launch { emit(it.sha to getPrDetailsByCommit(token, it.sha).get()) }
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
