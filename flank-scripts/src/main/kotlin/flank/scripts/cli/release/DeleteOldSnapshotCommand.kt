package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.release.jfrog.jFrogDeleteOldSnapshot

class DeleteOldSnapshotCommand : CliktCommand(
    name = "jFrogDelete",
    help = "Delete old version on bintray"
) {
    private val version by option(help = "Maven version to delete").required()

    override fun run() {
        jFrogDeleteOldSnapshot(version)
    }
}
