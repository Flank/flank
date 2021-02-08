package flank.scripts.cli.assemble

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.cli.assemble.android.AndroidCommand
import flank.scripts.cli.assemble.ios.IosCommand

internal object AssembleCommand : CliktCommand(
    name = "assemble",
    help = "Group of commands to assemble application"
) {
    init {
        subcommands(
            AndroidCommand,
            FlankCommand,
            IosCommand,
            GoCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
