package ftl.cli.auth

import picocli.CommandLine

@CommandLine.Command(
    name = "login",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Obtains access credentials for your user account via a web-based authorization flow."],
    description = ["""Authenticates using your user account. For CI, a service account is recommended."""]
)
class LoginCommand : Runnable {
    override fun run() {
    }

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
