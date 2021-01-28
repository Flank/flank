package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.ios.createUniversalFrameworkFiles

object UniversalFrameworkCommand : CliktCommand(name = "iosUniversalFramework", help = "Create Universal Framework") {
    override fun run() {
        createUniversalFrameworkFiles()
    }
}
