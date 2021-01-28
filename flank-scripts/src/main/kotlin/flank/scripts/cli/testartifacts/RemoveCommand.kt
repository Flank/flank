package flank.scripts.cli.testartifacts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import flank.scripts.ops.testartifacts.Context
import flank.scripts.ops.testartifacts.removeRemoteCopy

object RemoveCommand : CliktCommand(
    help = "Remove remote copy of test artifacts."
) {
    val artifacts by requireObject<Context>()
    override fun run() {
        artifacts.removeRemoteCopy()
    }
}
