package ftl.cli.firebase.test

import ftl.cli.firebase.test.providedsoftware.ProvidedSoftwareListCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "provided-software",
    synopsisHeading = "",
    subcommands = [
        ProvidedSoftwareListCommand::class
    ],
    usageHelpAutoWidth = true
)
class ProvidedSoftwareCommand : Runnable {
    override fun run() {
        CommandLine.usage(ProvidedSoftwareCommand(), System.out)
    }
}
