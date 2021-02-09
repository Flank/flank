package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.buildexample.ios.buildIosExample

object ExampleCommand : CliktCommand(
    name = "example",
    help = "Assemble iOS example application"
) {
    override fun run() {
        buildIosExample()
    }
}
