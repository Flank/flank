package flank.scripts.integration

import flank.scripts.github.objects.GithubPullRequest

private val successTemplate = { lastRun: String, runId: String, url: String ->
    """
    |### Full suite IT run :white_check_mark: SUCCEEDED :white_check_mark:
    |**Timestamp:** $lastRun
    |**Job run:** [$runId](https://github.com/Flank/flank/actions/runs/$runId
    |**Build scan URL:** $url
    |**Closing issue**
""".trimMargin()
}

private val failureTemplate = { lastRun: String, runId: String, url: String ->
    """
    |### Full suite IT run :x: FAILED :x:
    |**Timestamp:** $lastRun
    |**Job run:** [$runId](https://github.com/Flank/flank/actions/runs/$runId
    |**Build scan URL:** $url
""".trimMargin()
}

sealed class CommentMessage
object Success : CommentMessage()
object Failure : CommentMessage()

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
