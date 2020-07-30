package flank.scripts.release.jfrog.delete

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.release.jfrog.flankMaven
import flank.scripts.utils.runCommand

class DeleteOldSnapshotCommand : CliktCommand(name = "jFrogDelete", help = "Delete old version on bintray") {

    private val version by option(help = "Maven version to delete").required()

    override fun run() {
        jFrogDeleteOldSnapshot(version)
    }
}

fun jFrogDeleteOldSnapshot(version: String) {
    "jfrog bt version-delete ${flankMaven(version)} --quiet".runCommand()
}
