package ftl.presentation.cli.corellium

import ftl.presentation.cli.corellium.test.AndroidCommand
import ftl.util.PrintHelpCommand
import picocli.CommandLine.Command

@Command(
    name = "test",
    synopsisHeading = "",
    subcommands = [
        AndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class TestCommand : PrintHelpCommand()
