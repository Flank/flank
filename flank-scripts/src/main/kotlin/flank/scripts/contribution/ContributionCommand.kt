package flank.scripts.contribution

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.contribution.githooks.GitHooksLinkCommand

object ContributionCommand : CliktCommand(name = "contribution", help = "Tasks for assisting with contribution") {
    init {
        subcommands(GitHooksLinkCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
