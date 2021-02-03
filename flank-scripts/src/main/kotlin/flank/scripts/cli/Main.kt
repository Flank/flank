package flank.scripts.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.cli.ci.CiCommand
import flank.scripts.cli.contribution.ContributionCommand
import flank.scripts.cli.dependencies.DependenciesCommand
import flank.scripts.cli.integration.IntegrationCommand
import flank.scripts.cli.pullrequest.PullRequestCommand
import flank.scripts.cli.release.ReleaseCommand
import flank.scripts.cli.shell.ShellCommand
import flank.scripts.cli.testartifacts.TestArtifactsCommand

class Main : CliktCommand(name = "flankScripts") {
    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}

fun main(args: Array<String>) {
    Main().subcommands(
        ReleaseCommand,
        CiCommand,
        DependenciesCommand,
        TestArtifactsCommand,
        ShellCommand,
        PullRequestCommand,
        IntegrationCommand,
        ContributionCommand
    ).main(args)
}
