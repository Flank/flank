package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.cli.github.DeleteOldTagCommand
import flank.scripts.cli.github.DeleteReleaseCommand
import flank.scripts.cli.github.MakeReleaseCommand

object ReleaseCommand : CliktCommand(
    name = "release",
    help = "Group of commands for creating Flank release"
) {
    init {
        subcommands(
            MakeReleaseCommand,
            DeleteReleaseCommand,
            DeleteOldTagCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
