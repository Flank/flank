package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.assemble.ios.buildFtl

object FtlCommand : CliktCommand(
    name = "ftl",
    help = "Assemble iOS ftl example application"
) {
    override fun run() {
        buildFtl()
    }
}
