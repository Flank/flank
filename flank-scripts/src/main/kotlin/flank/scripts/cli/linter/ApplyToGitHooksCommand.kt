package flank.scripts.cli.linter

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.ops.linter.linkGitHooks

object ApplyToGitHooksCommand : CliktCommand(
    name = "apply_to_git_hooks",
    help = "Apply Linter pre-commit hook"
) {
    override fun run() {
        linkGitHooks()
    }
}
