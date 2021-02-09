package flank.scripts.cli.dependencies

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.shell.buildexample.ios.createUniversalFrameworkFiles

object UniversalFrameworkCommand : CliktCommand(
    name = "universal_framework_files",
    help = "Create Universal Framework"
) {
    override fun run() {
        createUniversalFrameworkFiles()
    }
}
