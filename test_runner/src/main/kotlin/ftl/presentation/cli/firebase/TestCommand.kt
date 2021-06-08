package ftl.presentation.cli.firebase

import ftl.presentation.cli.firebase.test.AndroidCommand
import ftl.presentation.cli.firebase.test.IPBlocksCommand
import ftl.presentation.cli.firebase.test.IosCommand
import ftl.presentation.cli.firebase.test.NetworkProfilesCommand
import ftl.presentation.cli.firebase.test.ProvidedSoftwareCommand
import ftl.util.PrintHelp
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "test",
    synopsisHeading = "",
    subcommands = [
        AndroidCommand::class,
        IosCommand::class,
        NetworkProfilesCommand::class,
        ProvidedSoftwareCommand::class,
        IPBlocksCommand::class
    ],
    usageHelpAutoWidth = true
)
class TestCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
