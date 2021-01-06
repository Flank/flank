package flank.scripts.contribution

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.contribution.githooks.GitHooksLinkCommand
import flank.scripts.contribution.ideaktlint.IdeaKtlintCodeStyleCommand

object ContributionCommand : CliktCommand(name = "contribution", help = "Tasks for assisting with contribution") {
    init {
        subcommands(
            GitHooksLinkCommand,
            IdeaKtlintCodeStyleCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}
