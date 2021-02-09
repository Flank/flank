package flank.scripts.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import flank.scripts.cli.assemble.AssembleCommand
import flank.scripts.cli.dependencies.DependenciesCommand
import flank.scripts.cli.firebase.FirebaseCommand
import flank.scripts.cli.github.GitHubCommand
import flank.scripts.cli.integrationtests.IntegrationTestsCommand
import flank.scripts.cli.linter.LinterCommand
import flank.scripts.cli.release.ReleaseCommand
import flank.scripts.cli.testartifacts.TestArtifactsCommand

class Main : CliktCommand(name = "flankScripts") {
    @Suppress("EmptyFunctionBlock")
    override fun run() {}
}

fun main(args: Array<String>) {
    Main().subcommands(
        AssembleCommand,
        FirebaseCommand,
        GitHubCommand,
        DependenciesCommand,
        IntegrationTestsCommand,
        LinterCommand,
        ReleaseCommand,
        TestArtifactsCommand
    ).main(args)
}
