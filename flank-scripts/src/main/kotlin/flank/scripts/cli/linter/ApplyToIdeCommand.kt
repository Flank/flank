package flank.scripts.cli.linter

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.contribution.applyKtlintToIdea
import kotlinx.coroutines.runBlocking

object ApplyToIdeCommand : CliktCommand(
    name = "apply_to_ide",
    help = "Apply Linter to IDE"
) {
    override fun run(): Unit = runBlocking {
        applyKtlintToIdea()
    }
}
