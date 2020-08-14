package flank.scripts.ci

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.ci.nexttag.NextReleaseTagCommand
import flank.scripts.ci.releasenotes.GenerateReleaseNotesCommand

class CiCommand : CliktCommand(help = "Contains command related to CI", name = "ci") {

    init {
        subcommands(GenerateReleaseNotesCommand(), NextReleaseTagCommand())
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
