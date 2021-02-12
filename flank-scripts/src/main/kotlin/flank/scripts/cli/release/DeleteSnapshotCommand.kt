package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.jfrog.jFrogDeleteOldSnapshot

object DeleteSnapshotCommand : CliktCommand(
    name = "delete_snapshot",
    help = "Delete snapshot package from artifacts repository"
) {
    private val version by option(help = "Maven version to delete").required()

    override fun run() {
        jFrogDeleteOldSnapshot(version)
    }
}
