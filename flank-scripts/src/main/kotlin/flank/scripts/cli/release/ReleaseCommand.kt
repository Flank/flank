package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object ReleaseCommand : CliktCommand(
    name = "release",
    help = "Contains all release commands"
) {
    init {
        subcommands(
            ReleaseFlankCommand,
            DeleteOldSnapshotCommand,
            SyncMavenCommand,
            DeleteOldReleaseCommand,
            DeleteOldTagCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
