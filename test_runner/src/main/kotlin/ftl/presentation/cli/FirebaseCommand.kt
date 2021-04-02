package ftl.presentation.cli

import ftl.presentation.cli.firebase.CancelCommand
import ftl.presentation.cli.firebase.RefreshCommand
import ftl.presentation.cli.firebase.TestCommand
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
