package flank.scripts.contribution.ideaktlint

import com.github.ajalt.clikt.core.CliktCommand
import com.github.kittinunf.fuel.core.awaitUnit
import com.github.kittinunf.fuel.httpDownload
import flank.common.deleteFile
import flank.common.logLn
import flank.scripts.shell.utils.currentPath
import flank.scripts.shell.utils.goToRoot
import flank.scripts.utils.isWindows
import flank.scripts.utils.runCommand
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Paths

object IdeaKtlintCodeStyleCommand : CliktCommand(
    name = "applyKtlintToIdea",
    help = "Applies Ktlint to this idea project forcefully"
) {
    override fun run(): Unit = runBlocking {
        logLn("Applying Ktlint code style to this idea project")
        logLn("Retrieving Ktlint...")
        retrieveKtlintResolveCommand()
        applyKtlintToIdea()
    }

    private fun applyKtlintToIdea() {
        applyKtlintToIdeaCommand()
        tryCleanupKtlint()
    }

    private fun applyKtlintToIdeaCommand() = "java -jar ktlint applyToIDEAProject -y".runCommand(executionDirectory = getRootDirFile())

    private fun tryCleanupKtlint() {
        logLn("Cleanup Ktlint leftover files...")
        (getKtlintFilePath()).deleteFile()
    }

    private suspend fun retrieveKtlintResolveCommand() = "https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint"
        .httpDownload().fileDestination { _, _ ->
            Files.createFile(Paths.get(getKtlintFilePath())).toFile().also {
                logLn("Ktlint written to: ${it.absolutePath}")
            }
        }.awaitUnit()

    private fun getKtlintFilePath() = getRootPathString() + (if (isWindows) "\\" else "/") + "ktlint"
    private fun getRootPathString() = goToRoot(currentPath).toAbsolutePath().toString()
    private fun getRootDirFile() = goToRoot(currentPath).toAbsolutePath().toFile()
}
