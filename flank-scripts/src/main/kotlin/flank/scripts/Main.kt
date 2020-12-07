package flank.scripts

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.ci.CiCommand
import flank.scripts.dependencies.DependenciesCommand
import flank.scripts.integration.IntegrationCommand
import flank.scripts.pullrequest.PullRequestCommand
import flank.scripts.release.ReleaseCommand
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
        IntegrationCommand
    ).main(args)
}
