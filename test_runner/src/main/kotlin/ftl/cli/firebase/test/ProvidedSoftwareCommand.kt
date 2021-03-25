package ftl.cli.firebase.test

import ftl.cli.firebase.test.providedsoftware.ProvidedSoftwareListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "provided-software",
    synopsisHeading = "",
    subcommands = [
        ProvidedSoftwareListCommand::class
    ],
    usageHelpAutoWidth = true
)
class ProvidedSoftwareCommand : PrintHelp
