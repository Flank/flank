package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.release.hub.deleteOldRelease

class DeleteOldReleaseCommand : CliktCommand(name = "deleteOldRelease", help = "Delete old release on github") {

    private val gitTag by option(help = "Git Tag").required()

    override fun run() {
        deleteOldRelease(gitTag)
    }
}
