package flank.scripts.cli.integrationtests

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

object IntegrationTestsCommand : CliktCommand(
    name = "integration_tests",
    help = "Group of commands for handling integration tests"
) {
    init {
        subcommands(ProcessResultCommand)
    }

    @Suppress("EmptyFunctionBlock")
    override fun run() {
    }
}
