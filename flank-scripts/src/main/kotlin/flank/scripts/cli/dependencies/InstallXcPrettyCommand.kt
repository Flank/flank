package flank.scripts.cli.dependencies

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.installXcPretty

object InstallXcPrettyCommand : CliktCommand(
    name = "install_xcpretty",
    help = "Install xcpretty formatter"
) {
    override fun run() {
        installXcPretty()
    }
}
