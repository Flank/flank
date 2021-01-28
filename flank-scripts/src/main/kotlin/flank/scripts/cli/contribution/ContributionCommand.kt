package flank.scripts.cli.contribution

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object ContributionCommand : CliktCommand(
    name = "contribution",
    help = "Tasks for assisting with contribution"
) {
    init {
        subcommands(
            GitHooksLinkCommand,
            IdeaKtlintCodeStyleCommand
        )
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
