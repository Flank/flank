package flank.scripts.integration

import com.github.kittinunf.result.getOrNull
import flank.scripts.github.getGitHubWorkflowRunsSummary
import java.time.Instant
import java.time.format.DateTimeFormatter

suspend fun getLastITWorkflowRunDate(token: String) = getLastWorkflowRunDate(
    token = token,
    workflowFileName = "full_suite_integration_tests.yml"
)

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
            ?.also {
                println(
                    """
            ** Last workflow run:
                 name: ${it.name}
                 last run: ${it.createdAt}
                 url: ${it.htmlUrl}
        """.trimIndent()
                )
            }?.createdAt.run { DateTimeFormatter.ISO_INSTANT.format(Instant.parse(this)) }
    } ?: run {
    println("** No workflow run found for ${workflowName ?: workflowFileName}")
    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
}
