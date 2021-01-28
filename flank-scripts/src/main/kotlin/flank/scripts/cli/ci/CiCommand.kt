package flank.scripts.cli.ci

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object CiCommand : CliktCommand(
    help = "Contains command related to CI",
    name = "ci"
) {
    init {
        subcommands(GenerateReleaseNotesCommand, NextReleaseTagCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
