package flank.scripts.ops.linter

import flank.common.deleteFile
import flank.common.downloadFile
import flank.common.logLn
import flank.scripts.utils.getKtlintFilePath
import flank.scripts.utils.getRootDirFile
import flank.scripts.utils.runCommand

suspend fun applyKtlintToIdea() {
    logLn("Applying Ktlint code style to this idea project")
    logLn("Retrieving Ktlint...")
    retrieveKtlintResolveCommand()
    applyKtlintToIdeaCommand()
    tryCleanupKtlint()
}

private suspend fun retrieveKtlintResolveCommand() =
    "https://github.com/pinterest/ktlint/releases/download/0.40.0/ktlint".downloadFile(getKtlintFilePath())

private fun applyKtlintToIdeaCommand() =
    "java -jar ktlint applyToIDEAProject -y".runCommand(executionDirectory = getRootDirFile())

private fun tryCleanupKtlint() {
    logLn("Cleanup Ktlint leftover files...")
    (getKtlintFilePath()).deleteFile()
}
