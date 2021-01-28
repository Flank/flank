package flank.scripts.cli.shell.buildexample

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.ios.buildIosExample

object BuildExampleCommand : CliktCommand(
    name = "iosBuildExample",
    help = "Build example ios app"
) {
    override fun run() {
        buildIosExample()
    }
}
