package ftl.presentation.cli.firebase.test.networkprofiles

import ftl.domain.ListNetworkProfiles
import ftl.domain.invoke
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
    Runnable,
    ListNetworkProfiles {
    override fun run() = invoke()
}
