package flank.scripts.release.jfrog.delete

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required

class DeleteOldSnapshotCommand : CliktCommand(name = "jfrogDelete", help = "Delete old version on bintray") {

    private val version by option().required()

    override fun run() {
        jFrogDeleteOldSnapshot(version)
    }
}
