@file:Suppress("EXPERIMENTAL_API_USAGE")

package flank.scripts.integration

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import kotlinx.coroutines.runBlocking

object ProcessResultCommand : CliktCommand(name = "processResults") {

    private val itResult by option(help = "IT run job status", names = arrayOf("--result"))
        .enum<ITResults>(ignoreCase = true)
        .required()

    private val buildScanURL by option(help = "Gradle build scan URL", names = arrayOf("--url"))
        .required()

    private val githubToken by option(help = "Git Token").required()
    private val runID by option(help = "Workflow job ID").required()
    private val openedIssue by lazy { runBlocking { checkForOpenedITIssues(githubToken) } }
    private val lastRun by lazy { runBlocking { getLastITWorkflowRunDate(githubToken) } }

    override fun run() {
        runBlocking {
            logArgs()
            with(makeContext()) {
                when {
                    itResult == ITResults.FAILURE && openedIssue == null -> createNewIssue()
                    itResult == ITResults.FAILURE && openedIssue != null -> postComment()
                    itResult == ITResults.SUCCESS && openedIssue != null -> closeIssue()
                    else -> return@runBlocking
                }
            }
        }
    }

    private fun makeContext() = IntegrationContext(
        result = itResult,
        token = githubToken,
        url = buildScanURL,
        runID = runID,
        lastRun = lastRun,
        openedIssue = openedIssue
    )

    private fun logArgs() = println(
        """
        ** Parameters:
             result: $itResult
             url:    $buildScanURL
             runID:  $runID
    """.trimIndent()
    )
}

enum class ITResults {
    SUCCESS, FAILURE
}
