package flank.scripts.cli.github

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object GitHubCommand : CliktCommand(
    name = "github",
    help = "Group of command for managing GitHub integration"
) {
    init {
        subcommands(
            CopyIssuePropertiesCommand,
            DeleteReleaseCommand,
            DeleteOldTagCommand,
            MakeReleaseCommand,
            DownloadFlankCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
