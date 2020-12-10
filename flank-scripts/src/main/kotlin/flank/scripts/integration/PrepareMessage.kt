package flank.scripts.integration

import flank.scripts.github.objects.GithubPullRequest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val successTemplate = { lastRun: String, runId: String, url: String ->
    """
    |### Full suite IT run :white_check_mark: SUCCEEDED :white_check_mark:
    |**Timestamp:** ${makeHumanFriendly(lastRun)}
    |**Job run:** [$runId](https://github.com/Flank/flank/actions/runs/$runId
    |**Build scan URL:** $url
    |**Closing issue**
""".trimMargin()
}

private val failureTemplate = { lastRun: String, runId: String, url: String ->
    """
    |### Full suite IT run :x: FAILED :x:
    |**Timestamp:** ${makeHumanFriendly(lastRun)}
    |**Job run:** [$runId](https://github.com/Flank/flank/actions/runs/$runId
    |**Build scan URL:** $url
""".trimMargin()
}

private fun makeHumanFriendly(date: String) =
    LocalDateTime
        .ofInstant(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(date)), ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

fun prepareSuccessMessage(
    lastRun: String,
    runId: String,
    url: String
): String = successTemplate(lastRun, runId, url)

fun prepareFailureMessage(
    lastRun: String,
    runId: String,
    url: String,
    prs: List<Pair<String, GithubPullRequest?>>
): String = buildString {
    appendLine(failureTemplate(lastRun, runId, url))
    if (prs.isEmpty()) appendLine("No new commits")
    else {
        appendLine("|commit SHA|PR|")
        appendLine("|---|:---:|")
        prs.forEach { (commit, pr) ->
            appendLine("|$commit|${pr?.let { "[${it.title}](${it.htmlUrl})" } ?: "---"}")
        }
    }
}
