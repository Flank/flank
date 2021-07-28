package ftl.presentation.cli.auth

import flank.common.newLine
import flank.common.startWithNewLine
import ftl.api.LoginState
import ftl.domain.LoginGoogleAccount
import ftl.domain.invoke
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelpCommand
import kotlinx.coroutines.runBlocking
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

    override fun run() = runBlocking { invoke() }

    override val out = outputLogger {
        when (this) {
            is LoginState.LoginStarted -> "Visit the following URL in your browser:$newLine$url"
            is LoginState.LoginFinished -> "User token saved to $tokenLocation".startWithNewLine()
            else -> throwUnknownType()
        }
    }
}
