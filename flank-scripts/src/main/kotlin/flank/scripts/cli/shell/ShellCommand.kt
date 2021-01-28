package flank.scripts.cli.shell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.cli.shell.buildexample.BuildExampleCommand
import flank.scripts.cli.shell.buildexample.OpsCommand
import flank.scripts.cli.shell.firebase.FirebaseCommand

object ShellCommand : CliktCommand(name = "shell", help = "Task for shell commands") {
    init {
        subcommands(
            BuildFlankCommand,
            FirebaseCommand,
            OpsCommand,
            BuildExampleCommand,
            BuildFtlCommand,
            InstallXcPrettyCommand,
            RunFtlLocalCommand,
            SetupIosEnvCommand,
            UniversalFrameworkCommand,
            UpdateBinariesCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
