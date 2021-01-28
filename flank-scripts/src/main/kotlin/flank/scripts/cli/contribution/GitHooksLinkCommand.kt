package flank.scripts.cli.contribution

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.contribution.linkGitHooks

object GitHooksLinkCommand : CliktCommand(
    name = "linkGitHooks",
    help = "Creates a link for pre-commit hook for automatic linting"
) {
    override fun run() {
        linkGitHooks()
    }
}
