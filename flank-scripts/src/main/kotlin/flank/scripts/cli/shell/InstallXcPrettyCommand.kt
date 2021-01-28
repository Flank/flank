package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.installXcPretty

object InstallXcPrettyCommand : CliktCommand(name = "install_xcpretty", help = "Build ios app with tests") {
    override fun run() {
        installXcPretty()
    }
}
