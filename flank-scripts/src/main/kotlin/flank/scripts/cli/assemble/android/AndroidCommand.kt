package flank.scripts.cli.assemble.android

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

internal object AndroidCommand : CliktCommand(
    name = "android",
    help = "Subgroup of commands for Android test application assembly"
) {
    init {
        subcommands(AppCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
