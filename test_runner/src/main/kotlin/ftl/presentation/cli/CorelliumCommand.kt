package ftl.presentation.cli

import ftl.presentation.cli.corellium.TestCommand
import ftl.util.PrintHelp
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "corellium",
    synopsisHeading = "",
    subcommands = [
        TestCommand::class
    ],
    usageHelpAutoWidth = true
)
class CorelliumCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
