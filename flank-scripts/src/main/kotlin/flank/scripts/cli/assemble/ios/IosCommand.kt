package flank.scripts.cli.assemble.ios

import com.github.ajalt.clikt.core.CliktCommand

object IosCommand : CliktCommand(
    name = "ios",
    help = "Subgroup of commands for iOS test applications assembly." +
        "Without arguments or subcommands it assembles all iOS applications"
) {
    override fun run() = listOf(
        EarlGreyCommand,
        ExampleCommand,
        FlankExampleCommand,
        FtlCommand,
        GameLoopExampleCommand,
        RunFtlLocalCommand,
        TestPlansExample
    ).forEach(CliktCommand::run)
}
