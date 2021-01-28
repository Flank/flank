package flank.scripts.cli.ci

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.google.common.annotations.VisibleForTesting
import flank.scripts.ops.ci.createReleaseNotes

object GenerateReleaseNotesCommand : CliktCommand(
    help = "Command to append item to release notes",
    name = "generateReleaseNotes"
) {
    private val token by option(help = "Git Token").required()

    @VisibleForTesting
    internal val releaseNotesFile by option(help = "Path to release_notes.md")
        .default("release_notes.md")

    override fun run() {
        createReleaseNotes(token, releaseNotesFile)
    }
}
