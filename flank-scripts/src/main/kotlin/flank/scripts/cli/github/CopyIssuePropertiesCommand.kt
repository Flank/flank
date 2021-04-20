package flank.scripts.cli.github

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import flank.scripts.ops.github.copyGitHubProperties

object CopyIssuePropertiesCommand : CliktCommand(
    name = "copy_issue_properties",
    help = "Copy properties(assignees, story points, labels) from issue to pull request"
) {
    private val githubToken by option(help = "Git Token").required()
    private val prNumber by option(help = "Pull request number").int().required()

    override fun run() {
        copyGitHubProperties(githubToken, prNumber)
    }
}
