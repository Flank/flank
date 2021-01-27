package flank.scripts.cli.contribution

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.common.logLn
import flank.scripts.ops.contribution.applyKtlintToIdea
import flank.scripts.ops.contribution.linkGitHooks
import kotlinx.coroutines.runBlocking

object ContributionCommand : CliktCommand(name = "contribution", help = "Tasks for assisting with contribution") {
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

object GitHooksLinkCommand : CliktCommand(
    name = "linkGitHooks",
    help = "Creates a link for pre-commit hook for automatic linting"
) {
    override fun run() {
        logLn("Linking Githooks.")
        linkGitHooks()
    }
}

object IdeaKtlintCodeStyleCommand : CliktCommand(
    name = "applyKtlintToIdea",
    help = "Applies Ktlint to this idea project forcefully"
) {
    override fun run(): Unit = runBlocking {
        logLn("Applying Ktlint code style to this idea project")
        logLn("Retrieving Ktlint...")
        applyKtlintToIdea()
    }
}
