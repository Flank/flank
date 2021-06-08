package ftl.presentation.cli

import ftl.presentation.cli.auth.LoginCommand
import ftl.util.PrintHelp
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "auth",
    synopsisHeading = "%n",
    header = ["Manage oauth2 credentials for Google Cloud"],
    subcommands = [
        LoginCommand::class
    ],
    usageHelpAutoWidth = true
)
class AuthCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
