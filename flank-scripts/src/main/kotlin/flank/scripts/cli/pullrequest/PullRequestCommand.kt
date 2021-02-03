package flank.scripts.cli.pullrequest

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object PullRequestCommand : CliktCommand(
    name = "pullRequest"
) {
    init {
        subcommands(CopyPropertiesCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
