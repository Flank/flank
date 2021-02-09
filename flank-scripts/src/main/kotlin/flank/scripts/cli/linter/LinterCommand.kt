package flank.scripts.cli.linter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object LinterCommand : CliktCommand(
    name = "linter",
    help = "Group of commands used for applying correct coding style"
) {
    init {
        subcommands(
            ApplyToGitHooksCommand,
            ApplyToIdeCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
