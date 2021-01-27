package flank.scripts.cli.ci

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import flank.scripts.ops.ci.createNextReleaseTag
import flank.scripts.ops.ci.createReleaseNotes

class CiCommand : CliktCommand(help = "Contains command related to CI", name = "ci") {

    init {
        subcommands(GenerateReleaseNotesCommand(), NextReleaseTagCommand())
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}

class GenerateReleaseNotesCommand :
    CliktCommand("Command to append item to release notes", name = "generateReleaseNotes") {

    private val token by option(help = "Git Token").required()
    private val releaseNotesFile by option(help = "Path to release_notes.md").default("release_notes.md")

    override fun run() {
        createReleaseNotes(token, releaseNotesFile)
    }
}

class NextReleaseTagCommand : CliktCommand(help = "Print next release tag", name = "nextReleaseTag") {

    private val token by option(help = "Git Token").required()

    override fun run() {
        createNextReleaseTag(token)
    }
}
