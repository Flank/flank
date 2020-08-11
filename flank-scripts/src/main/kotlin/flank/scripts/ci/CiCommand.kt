package flank.scripts.ci

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.ci.releasenotes.AppendReleaseNotesCommand

class CiCommand : CliktCommand(help = "Contains command related to CI", name = "ci") {

    init {
        subcommands(AppendReleaseNotesCommand())
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
