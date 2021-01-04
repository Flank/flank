package flank.scripts.github.commons

import com.github.kittinunf.result.getOrNull
import flank.scripts.github.getGitHubWorkflowRunsSummary
import flank.scripts.github.objects.GitHubWorkflowRun
import kotlinx.coroutines.runBlocking
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
    ) { "** Missing either workflow name or workflow file name. Both can not be null" },
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

fun main() {
    runBlocking {
        getLastWorkflowRunDate(
            token = "46b81695ce4a71ca20e7d618d20c75bcc6a5268e",
            workflowFileName = "full_suite_integration_tests.yml"
        )
    }
}
