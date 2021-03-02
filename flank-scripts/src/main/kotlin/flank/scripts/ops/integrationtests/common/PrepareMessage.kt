package flank.scripts.ops.integrationtests.common

import flank.common.config.flankRepository
import flank.scripts.data.github.objects.GithubPullRequest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

internal fun prepareSuccessMessage(
    lastRun: String,
    runId: String,
    runState: ITRunState
): String = successTemplate(lastRun, runId, runState)

internal fun prepareFailureMessage(
    lastRun: String,
    runId: String,
    runState: ITRunState,
    prs: List<Pair<String, GithubPullRequest?>>
): String = buildString {
    appendLine(failureTemplate(lastRun, runId, runState))
    if (prs.isEmpty()) appendLine("No new commits")
    else {
        appendLine("|commit SHA|PR|")
        appendLine("|---|:---:|")
        prs.forEach { (commit, pr) ->
            appendLine("|$commit|${pr?.let { "[${it.title}](${it.htmlUrl})" } ?: "---"}")
        }
    }
}

private val successTemplate = { lastRun: String, runId: String, runState: ITRunState ->
    """
    |### Full suite IT run :white_check_mark: SUCCEEDED :white_check_mark:
    |**Timestamp:** ${makeHumanFriendly(lastRun)}
    |**Job run:** [$runId](https://github.com/$flankRepository/actions/runs/$runId)
    |**Windows url:** ${runState.windowsBSUrl.withDefault()}
    |**Linux url:** ${runState.linuxBSUrl.withDefault()}
    |**MacOs url:** ${runState.macOsBSUrl.withDefault()}
    |**Closing issue**
""".trimMargin()
}

private val failureTemplate = { lastRun: String, runId: String, runState: ITRunState ->
    """
    |### Full suite IT run :x: FAILED :x:
    |**Timestamp:** ${makeHumanFriendly(lastRun)}
    |**Job run:** [$runId](https://github.com/$flankRepository/actions/runs/$runId)
    ${runState.failureResult()}
""".trimMargin()
}

private fun ITRunState.failureResult() = """
    |**Windows status:**  $windowsResult  - url: ${windowsBSUrl.withDefault()}
    |**Linux status:**    $linuxResult  - url: ${linuxBSUrl.withDefault()}
    |**MacOs status:**    $macOsResult  - url: ${macOsBSUrl.withDefault()}
""".trimIndent()

private fun String.withDefault() = if (this.isBlank()) "N/A" else this

private fun makeHumanFriendly(date: String) =
    LocalDateTime
        .ofInstant(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(date)), ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
