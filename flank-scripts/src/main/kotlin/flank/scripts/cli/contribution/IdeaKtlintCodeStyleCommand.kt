package flank.scripts.cli.contribution

import com.github.ajalt.clikt.core.CliktCommand
import flank.common.logLn
import flank.scripts.ops.contribution.applyKtlintToIdea
import kotlinx.coroutines.runBlocking

object IdeaKtlintCodeStyleCommand : CliktCommand(
    name = "applyKtlintToIdea",
    help = "Applies Ktlint to this idea project forcefully"
) {
    override fun run(): Unit = runBlocking {
        logLn("Applying Ktlint code style to this idea project")
        logLn("Retrieving Ktlint...")
        applyKtlintToIdea()
    }
}
