package flank.scripts.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.ops.shell.BuildFlankCommand
import flank.scripts.ops.shell.ios.BuildExampleCommand
import flank.scripts.ops.shell.ios.BuildFtlCommand
import flank.scripts.ops.shell.ios.InstallXcPrettyCommand
import flank.scripts.ops.shell.ios.RunFtlLocalCommand
import flank.scripts.ops.shell.ios.SetupIosEnvCommand
import flank.scripts.ops.shell.ios.UniversalFrameworkCommand
import flank.scripts.ops.shell.ops.OpsCommand
import flank.scripts.ops.shell.updatebinaries.UpdateBinariesCommand

object ShellCommand : CliktCommand(name = "shell", help = "Task for shell commands") {
    init {
        subcommands(
            FirebaseCommand,
            BuildExampleCommand,
            BuildFtlCommand,
            RunFtlLocalCommand,
            UniversalFrameworkCommand,
            OpsCommand,
            UpdateBinariesCommand,
            BuildFlankCommand,
            InstallXcPrettyCommand,
            SetupIosEnvCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
