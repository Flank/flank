package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.buildFlank

object BuildFlankCommand : CliktCommand(name = "buildFlank", help = "Build Flank") {
    override fun run() {
        buildFlank()
    }
}
