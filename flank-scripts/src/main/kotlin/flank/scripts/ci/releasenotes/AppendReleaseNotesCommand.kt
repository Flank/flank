package flank.scripts.ci.releasenotes

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import java.io.File

class AppendReleaseNotesCommand : CliktCommand("Command to append item to release notes", name = "releaseNotesUpdate") {

    private val prNumber by option(help = "Number of closed PR").int().required()
    private val gitUser by option(help = "User login which merge changes").required()
    private val mergedPrTitle by option(help = "Merged PR title").required()

    internal val releaseNotesFile
            by option(help = "Path to release_notes.md").default("./release_notes.md")

    override fun run() {
        File(releaseNotesFile).appendReleaseNotes(prNumber, mergedPrTitle, gitUser)
    }
}
