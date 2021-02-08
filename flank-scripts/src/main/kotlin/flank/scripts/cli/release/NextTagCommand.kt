package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.ci.createNextReleaseTag

object NextTagCommand : CliktCommand(
    name = "next_tag",
    help = "Get tag for next release"
) {
    private val token by option(help = "Git Token").required()

    override fun run() {
        createNextReleaseTag(token)
    }
}
