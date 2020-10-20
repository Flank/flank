package flank.scripts.shell

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.shell.firebase.FirebaseCommand
import flank.scripts.shell.ios.BuildExampleCommand
import flank.scripts.shell.ios.BuildFtlCommand
import flank.scripts.shell.ios.RunFtlLocalCommand
import flank.scripts.shell.ios.UniversalFrameworkCommand
import flank.scripts.shell.ops.OpsCommand
import flank.scripts.shell.updatebinaries.UpdateBinariesCommand

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
            BuildFlankCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
