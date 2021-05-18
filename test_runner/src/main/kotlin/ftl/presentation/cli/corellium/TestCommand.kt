package ftl.presentation.cli.corellium

import ftl.presentation.cli.firebase.test.AndroidCommand
import ftl.util.PrintHelp
import picocli.CommandLine.Command

@Command(
    name = "test",
    synopsisHeading = "",
    subcommands = [
        AndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class TestCommand : PrintHelp
