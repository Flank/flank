package ftl.presentation.cli.corellium.test

import flank.corellium.cli.RunTestCorelliumAndroidCommand
import ftl.util.PrintHelp
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "android",
    synopsisHeading = "",
    subcommands = [
        RunTestCorelliumAndroidCommand::class
    ],
    usageHelpAutoWidth = true
)
class AndroidCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
