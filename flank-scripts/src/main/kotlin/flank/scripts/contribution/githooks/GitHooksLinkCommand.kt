package flank.scripts.contribution.githooks

import com.github.ajalt.clikt.core.CliktCommand
import flank.common.logLn
import flank.scripts.utils.runCommand

object GitHooksLinkCommand : CliktCommand(
    name = "linkGitHooks",
    help = "Creates a link for pre-commit hook for automatic linting"
) {
    override fun run() {
        logLn("Linking Githooks.")
        linkGitHooks()
    }

    private fun linkGitHooks() = "git config --local core.hooksPath .githooks/".runCommand()
}
