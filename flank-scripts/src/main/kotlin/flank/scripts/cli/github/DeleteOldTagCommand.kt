package flank.scripts.cli.github

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.github.tryDeleteOldTag

object DeleteOldTagCommand : CliktCommand(
    name = "delete_old_tag",
    help = "Delete old tag on GitHub"
) {
    private val gitTag by option(help = "Git Tag").required()
    private val username by option(help = "Git User").required()
    private val token by option(help = "Git Token").required()

    override fun run() {
        tryDeleteOldTag(gitTag, username, token)
    }
}
