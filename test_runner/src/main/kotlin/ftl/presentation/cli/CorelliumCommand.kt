package ftl.presentation.cli

import ftl.presentation.cli.corellium.TestCommand
import ftl.util.PrintHelp
import picocli.CommandLine.Command

@Command(
    name = "corellium",
    synopsisHeading = "",
    subcommands = [
        TestCommand::class
    ],
    usageHelpAutoWidth = true
)
class CorelliumCommand : PrintHelp()
