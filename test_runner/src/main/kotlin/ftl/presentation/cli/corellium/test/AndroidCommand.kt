package ftl.presentation.cli.corellium.test

import flank.corellium.cli.RunTestCorelliumAndroidCommand
import ftl.util.PrintHelp
import picocli.CommandLine.Command

@Command(
    name = "android",
    synopsisHeading = "",
    subcommands = [
        RunTestCorelliumAndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : PrintHelp
