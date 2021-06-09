package ftl.presentation.cli.firebase.test.networkprofiles

import ftl.api.NetworkProfile
import ftl.domain.ListNetworkProfiles
import ftl.domain.invoke
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelp
import ftl.util.asList
import ftl.util.asListOrNull
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["List all network profiles available for testing "],
    usageHelpAutoWidth = true
)
class NetworkProfilesListCommand :
    PrintHelp(),
    ListNetworkProfiles {

    override fun run() = invoke()

    override val out = outputLogger {
        when {
            asListOrNull<NetworkProfile>() != null -> asList<NetworkProfile>().toCliTable()
            this is String -> this
            else -> throwUnknownType()
        }
    }
}
