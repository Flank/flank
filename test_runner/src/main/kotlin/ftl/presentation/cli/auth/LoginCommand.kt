package ftl.presentation.cli.auth

import ftl.domain.LoginGoogleAccount
import ftl.domain.invoke
import ftl.util.PrintHelpCommand
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
    description = ["""Authenticates using your user account. For CI, a service account is recommended."""],
    usageHelpAutoWidth = true
)
class LoginCommand :
    PrintHelpCommand(),
    LoginGoogleAccount {

    override fun run() = invoke()
}
