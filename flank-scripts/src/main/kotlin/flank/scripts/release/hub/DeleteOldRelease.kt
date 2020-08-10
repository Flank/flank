package flank.scripts.release.hub

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.utils.runCommand

class DeleteOldReleaseCommand : CliktCommand(name = "deleteOldRelease", help = "Delete old release on github") {

    private val gitTag by option(help = "Git Tag").required()

    override fun run() {
        deleteOldRelease(gitTag)
    }
}

fun deleteOldRelease(tag: String) = "$DELETE_RELEASE_COMMAND $tag\".runCommand()

private const val DELETE_RELEASE_COMMAND = "hub release delete"
