package flank.scripts.cli.ci

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.ci.createNextReleaseTag

object NextReleaseTagCommand : CliktCommand(
    help = "Print next release tag",
    name = "nextReleaseTag"
) {
    private val token by option(help = "Git Token").required()

    override fun run() {
        createNextReleaseTag(token)
    }
}
