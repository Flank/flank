package ftl.cli

import ftl.cli.firebase.CancelCommand
import ftl.cli.firebase.RefreshCommand
import ftl.cli.firebase.TestCommand
import ftl.util.PrintHelp
import picocli.CommandLine.Command

@Command(
    name = "firebase",
    synopsisHeading = "",
    subcommands = [
        TestCommand::class,
        RefreshCommand::class,
        CancelCommand::class
    ],
    usageHelpAutoWidth = true
)
class FirebaseCommand : PrintHelp
