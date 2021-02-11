package flank.scripts.cli.release

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.google.common.annotations.VisibleForTesting
import flank.scripts.ops.release.createReleaseNotes

object GenerateReleaseNotesCommand : CliktCommand(
    name = "generate_release_notes",
    help = "Generate release notes"
) {
    private val token by option(help = "Git Token").required()

    @VisibleForTesting
    internal val releaseNotesFile by option(help = "Path to release_notes.md")
        .default("release_notes.md")

    override fun run() {
        createReleaseNotes(token, releaseNotesFile)
    }
}
