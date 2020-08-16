package ftl.cli.firebase

import ftl.cli.firebase.test.*
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "test",
    synopsisHeading = "",
    subcommands = [
        AndroidCommand::class,
        IosCommand::class,
        NetworkProfilesCommand::class,
        ProvidedSoftwareCommand::class,
        IPBlocksCommand::class
    ],
    usageHelpAutoWidth = true
)
class TestCommand : Runnable {
    override fun run() {
        CommandLine.usage(TestCommand(), System.out)
    }
}
