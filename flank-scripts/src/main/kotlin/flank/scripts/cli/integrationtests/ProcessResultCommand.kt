package flank.scripts.cli.integrationtests

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import flank.scripts.ops.integrationtests.common.ITResult
import flank.scripts.ops.integrationtests.processIntegrationTestsResult

object ProcessResultCommand : CliktCommand(
    name = "process_results",
    help = "Process results of integration tests"
) {
    private val itGlobalResult by option(
        help = "Global IT run job status of the matrix",
        names = arrayOf("--global-result", "--gr")
    ).enum<ITResult>(ignoreCase = true).required()

    private val envResult by option(
        help = "Stored results of individual runs - expecting JSON string",
        names = arrayOf("--run-result", "--rr")
    ).default("")

    private val validateRunResult: String
        get() = envResult.ifBlank { "No run data found" }

    private val githubToken by option(help = "Git Token").required()
    private val runID by option(help = "Workflow job ID").required()

    override fun run() {
        processIntegrationTestsResult(
            result = itGlobalResult,
            githubToken = githubToken,
            runState = validateRunResult,
            runID = runID
        )
    }
}
