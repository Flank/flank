package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.ios.buildFtl

object BuildFtlCommand : CliktCommand(
    name = "iosBuildFtl",
    help = "Build ftl ios app"
) {
    override fun run() {
        buildFtl()
    }
}
