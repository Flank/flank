package ftl.cli.firebase

import ftl.cli.firebase.test.AndroidCommand
import ftl.cli.firebase.test.IPBlocksCommand
import ftl.cli.firebase.test.IosCommand
import ftl.cli.firebase.test.NetworkProfilesCommand
import ftl.cli.firebase.test.ProvidedSoftwareCommand
import ftl.util.PrintHelp
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
class TestCommand : PrintHelp
