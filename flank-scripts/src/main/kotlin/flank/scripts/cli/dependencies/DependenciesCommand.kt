package flank.scripts.cli.dependencies

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object DependenciesCommand : CliktCommand(
    name = "dependencies",
    help = "Group of commands related to dependencies tasks"
) {
    init {
        subcommands(
            InstallXcPrettyCommand,
            SetupIosEnvCommand,
            UniversalFrameworkCommand,
            UpdateBinariesCommand,
            UpdateCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
