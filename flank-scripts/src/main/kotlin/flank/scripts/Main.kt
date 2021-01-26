package flank.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.cli.CiCommand
import flank.scripts.cli.ContributionCommand
import flank.scripts.cli.DependenciesCommand
import flank.scripts.cli.IntegrationCommand
import flank.scripts.cli.PullRequestCommand
import flank.scripts.cli.ReleaseCommand
import flank.scripts.shell.ShellCommand
import flank.scripts.testartifacts.TestArtifactsCommand

class Main : CliktCommand(name = "flankScripts") {
    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}

fun main(args: Array<String>) {
    Main().subcommands(
        ReleaseCommand(),
        CiCommand(),
        DependenciesCommand,
        TestArtifactsCommand(),
        ShellCommand,
        PullRequestCommand,
        IntegrationCommand,
        ContributionCommand
    ).main(args)
}
