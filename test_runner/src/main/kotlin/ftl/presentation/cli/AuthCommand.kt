package ftl.presentation.cli

import ftl.presentation.cli.auth.LoginCommand
import ftl.util.PrintHelpCommand
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
class AuthCommand : PrintHelpCommand()
