package flank.scripts.cli.shell.buildexample

import com.github.ajalt.clikt.core.CliktCommand

object IosOpsCommand : CliktCommand(
    name = "ios",
    help = "Build ios test artifacts"
) {

    override fun run() = listOf(
        BuildEarlGreyExampleCommand,
        BuildTestPlansExample,
        BuildFlankExampleCommand
    ).forEach(CliktCommand::run)
}
