package flank.scripts.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import flank.scripts.ops.integration.IntegrationContext
import flank.scripts.ops.integration.checkForOpenedITIssues
import flank.scripts.ops.integration.closeIssue
import flank.scripts.ops.integration.createNewIssue
import flank.scripts.ops.integration.getLastITWorkflowRunDate
import flank.scripts.ops.integration.postComment
import kotlinx.coroutines.runBlocking

object IntegrationCommand : CliktCommand(name = "integration") {

    init {
        subcommands(ProcessResultCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
object ProcessResultCommand : CliktCommand(name = "processResults") {

    private val itResult by option(help = "IT run job status", names = arrayOf("--result"))
        .enum<ITResults>(ignoreCase = true)
        .required()

    private val buildScanURL by option(help = "Gradle build scan URL", names = arrayOf("--url"))
        .default("")

    private val validatedURL: String
        get() = if (buildScanURL.isBlank()) "No build scan URL provided" else buildScanURL

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
        url = validatedURL,
        runID = runID,
        lastRun = lastRun,
        openedIssue = openedIssue
    )

    private fun logArgs() = println(
        """
        ** Parameters:
             result: $itResult
             url:    $validatedURL
             runID:  $runID
        """.trimIndent()
    )
}

enum class ITResults {
    SUCCESS, FAILURE
}
