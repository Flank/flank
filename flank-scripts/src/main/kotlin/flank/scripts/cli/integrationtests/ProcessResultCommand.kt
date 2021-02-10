package flank.scripts.cli.integrationtests

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.enum
import flank.scripts.ops.integrationtests.ITResults
import flank.scripts.ops.integrationtests.processIntegrationTestsResult

object ProcessResultCommand : CliktCommand(
    name = "process_results",
    help = "Process results of integration tests"
) {
    private val itResult by option(
        help = "IT run job status",
        names = arrayOf("--result")
    )
        .enum<ITResults>(ignoreCase = true)
        .required()

    private val buildScanURL by option(
        help = "Gradle build scan URL",
        names = arrayOf("--url")
    )
        .default("")

    private val validatedURL: String
        get() = if (buildScanURL.isBlank())
            "No build scan URL provided" else
            buildScanURL

    private val githubToken by option(help = "Git Token").required()
    private val runID by option(help = "Workflow job ID").required()

    override fun run() {
        processIntegrationTestsResult(
            result = itResult,
            githubToken = githubToken,
            url = validatedURL,
            runID = runID
        )
    }
}
