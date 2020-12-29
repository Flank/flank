package flank.scripts.contribution.ideaktlint

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kittinunf.fuel.httpDownload
import flank.common.deleteFile
import flank.common.logLn
import flank.scripts.utils.SUCCESS
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
        when (retrieveKtlintCurl()) {
            SUCCESS -> applyKtlintToIdea()
            else -> {
                tryCleanup()
                retrieveKtlintResolve()
            }
        }

    }

    private fun tryCleanup() = "ktlint".deleteFile()

    private fun retrieveKtlintCurl() = "curl -LO https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint"
        .runCommand()

    private fun retrieveKtlintResolve() = "https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint"
        .httpDownload().fileDestination { _, _ ->
            File.createTempFile("ktlint", "")
        }.response { result ->
            if (result.component2() == null) {
                applyKtlintToIdea()
            } else {
                logLn("Failed to apply Ktlint to idea project ${result.component2()?.localizedMessage.orEmpty()}")
            }
        }
    
    private fun applyKtlintToIdea() {
        logLn("Applying to Idea")
        "java -jar ktlint applyToIDEAProject".runCommand()
    }
}
