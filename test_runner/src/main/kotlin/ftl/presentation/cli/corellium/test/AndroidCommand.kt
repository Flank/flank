package ftl.presentation.cli.corellium.test

import flank.corellium.cli.RunTestCorelliumAndroidCommand
import ftl.util.PrintHelpCommand
import picocli.CommandLine.Command

@Command(
    name = "android",
    synopsisHeading = "",
    subcommands = [
        RunTestCorelliumAndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : PrintHelpCommand()
