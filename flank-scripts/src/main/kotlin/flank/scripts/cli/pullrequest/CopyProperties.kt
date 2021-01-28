package flank.scripts.cli.pullrequest

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import flank.scripts.ops.pullrequest.copyGitHubProperties

object CopyProperties :
    CliktCommand(name = "copyProperties", help = "Copy properties from referenced issue to pull request") {

    private val githubToken by option(help = "Git Token").required()
    private val zenhubToken by option(help = "ZenHub api Token").required()
    private val prNumber by option(help = "Pull request number").int().required()

    override fun run() {
        copyGitHubProperties(githubToken, zenhubToken, prNumber)
    }
}
