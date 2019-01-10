package ftl.cli

import ftl.cli.auth.LoginCommand
import picocli.CommandLine
import picocli.CommandLine.Command

@Command(
    name = "auth",
    synopsisHeading = "%n",
    header = ["Manage oauth2 credentials for Google Cloud"],
    subcommands = [
        LoginCommand::class
    ]
)
class AuthCommand : Runnable {
    override fun run() {
        CommandLine.usage(AuthCommand(), System.out)
    }
}
