package ftl.presentation.cli

import ftl.presentation.cli.firebase.CancelCommand
import ftl.presentation.cli.firebase.RefreshCommand
import ftl.presentation.cli.firebase.TestCommand
import ftl.util.PrintHelp
import picocli.CommandLine
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
class FirebaseCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
