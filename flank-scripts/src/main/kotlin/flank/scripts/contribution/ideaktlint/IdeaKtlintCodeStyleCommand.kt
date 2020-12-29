package flank.scripts.contribution.ideaktlint

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kittinunf.fuel.httpDownload
import flank.common.logLn
import flank.scripts.utils.runCommand
import kotlinx.coroutines.runBlocking
import java.io.File

object IdeaKtlintCodeStyleCommand : CliktCommand(
    name = "applyKtlintToIdea",
    help = "Applies Ktlint to this idea project forcefully"
) {
    override fun run(): Unit = runBlocking {
        logLn("Applying Ktlint code style to this idea project")
        logLn("Retrieving Ktlint...")
        val result = retrieveKtlintCurl()
        logLn("Applying to Idea")
        applyKtlintToIdea()
    }

    private fun retrieveKtlintCurl() = "curl -LO https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint".runCommand()
    private fun retrieveKtlintResolve() = "https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint"
        .httpDownload().fileDestination { _, _ -> File.createTempFile("ktlint", "") }
    private fun applyKtlintToIdea() = "java -jar ktlint applyToIDEAProject".runCommand()
}
