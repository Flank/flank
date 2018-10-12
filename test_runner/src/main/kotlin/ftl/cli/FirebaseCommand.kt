package ftl.cli

import ftl.cli.firebase.CancelCommand
import ftl.cli.firebase.RefreshCommand
import ftl.cli.firebase.TestCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "firebase",
    synopsisHeading = "",
    subcommands = [
        TestCommand::class,
        RefreshCommand::class,
        CancelCommand::class
    ]
)
class FirebaseCommand : Runnable {
    override fun run() {
        CommandLine.usage(FirebaseCommand(), System.out)
    }
}
