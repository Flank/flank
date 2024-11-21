package flank.scripts.data.github.commons

import com.github.kittinunf.result.getOrNull
import flank.scripts.data.github.getGitHubWorkflowRunsSummary
import flank.scripts.data.github.objects.GitHubWorkflowRun
import java.time.Instant
import java.time.format.DateTimeFormatter

suspend fun getLastWorkflowRunDate(
    token: String,
    workflowName: String? = null,
    workflowFileName: String? = null
): String = getGitHubWorkflowRunsSummary(
    githubToken = token,
    workflow = requireNotNull(
        workflowName ?: workflowFileName
    ) { "** Missing either workflow name or workflow file name. Both cannot be null" },
    parameters = listOf(
        "per_page" to 20,
        "page" to 1
    )
).getOrNull()
    ?.run {
        workflowRuns
            .filter { it.status != "in_progress" }
            .filter { it.conclusion != "cancelled" }
            .getOrNull(0)
            .logRun()
            ?.createdAt.run { DateTimeFormatter.ISO_INSTANT.format(Instant.parse(this)) }
    } ?: run {
    println("** No workflow run found for ${workflowName ?: workflowFileName}")
    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
}

private fun GitHubWorkflowRun?.logRun() = this?.also {
    println(
        """
** Last workflow run:
     name: ${it.name}
     last run: ${it.createdAt}
     url: ${it.htmlUrl}
        """.trimIndent()
    )
}
