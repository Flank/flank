package ftl.presentation.cli.corellium.test

import flank.corellium.cli.TestAndroidCommand
import ftl.util.PrintHelpCommand
import picocli.CommandLine.Command

@Command(
    name = "android",
    synopsisHeading = "",
    subcommands = [
        TestAndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : PrintHelpCommand()
