package flank.scripts.cli.github

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.github.deleteOldRelease

object DeleteReleaseCommand : CliktCommand(
    name = "delete_release",
    help = "Delete old release on github"
) {

    private val gitTag by option(help = "Git Tag").required()

    override fun run() {
        deleteOldRelease(gitTag)
    }
}
