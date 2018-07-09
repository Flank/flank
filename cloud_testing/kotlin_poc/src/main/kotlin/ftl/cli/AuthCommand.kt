package ftl.cli

import ftl.config.CredTmp
import ftl.config.YamlConfig
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import picocli.CommandLine

@CommandLine.Command(name = "auth",
        sortOptions = false,
        headerHeading = "",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = ["Authorize Firebase Test Lab run"],
        description = ["""Use Oauth to enter your credentials
"""])
class AuthCommand : Runnable {
    override fun run() {
        runBlocking {
            CredTmp.authorize()
        }
    }

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
