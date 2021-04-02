package ftl.presentation.cli

import ftl.util.PrintHelp
import picocli.CommandLine.Command

@Command(
    name = "auth",
    synopsisHeading = "%n",
    header = ["Manage oauth2 credentials for Google Cloud"],
    subcommands = [
        ftl.presentation.cli.auth.LoginCommand::class
    ],
    usageHelpAutoWidth = true
)
class AuthCommand : PrintHelp
