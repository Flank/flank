package flank.scripts.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.release.hub.DeleteOldReleaseCommand
import flank.scripts.release.hub.ReleaseFlankCommand
import flank.scripts.release.hub.DeleteOldTagCommand
import flank.scripts.release.jfrog.delete.DeleteOldSnapshotCommand
import flank.scripts.release.jfrog.sync.SyncMavenCommand
import flank.scripts.release.updatebugsnag.UpdateBugSnagCommand

class ReleaseCommand : CliktCommand(name = "release", help = "Contains all release commands") {

    init {
        subcommands(
                ReleaseFlankCommand(),
                UpdateBugSnagCommand(),
                DeleteOldSnapshotCommand(),
                SyncMavenCommand(),
                DeleteOldReleaseCommand(),
                DeleteOldTagCommand()
        )
    }

    override fun run() {}
}
