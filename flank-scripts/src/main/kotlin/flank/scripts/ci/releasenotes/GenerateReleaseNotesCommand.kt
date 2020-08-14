package flank.scripts.ci.releasenotes

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ci.getLatestReleaseTag
import flank.scripts.ci.nexttag.generateNextReleaseTag
import java.io.File
import kotlinx.coroutines.runBlocking

class GenerateReleaseNotesCommand :
    CliktCommand("Command to append item to release notes", name = "generateReleaseNotes") {

    private val token by option(help = "Git Token").required()
    internal val releaseNotesFile by option(help = "Path to release_notes.md").default("release_notes.md")

    override fun run() {
        runBlocking {
            val latestReleaseTag = getLatestReleaseTag(token)
            File(releaseNotesFile).appendReleaseNotes(
                messages = generateReleaseNotes(latestReleaseTag, token),
                releaseTag = generateNextReleaseTag(latestReleaseTag)
            )
        }
    }
}
