package flank.scripts.cli.assemble

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.buildFlank

object FlankCommand : CliktCommand(
    name = "flank",
    help = "Build Flank"
) {
    override fun run() {
        buildFlank()
    }
}
