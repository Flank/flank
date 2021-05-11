package ftl.presentation.cli.firebase.test.networkprofiles

import ftl.api.NetworkProfile
import ftl.domain.ListNetworkProfiles
import ftl.domain.invoke
import ftl.environment.common.toCliTable
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
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

    override val out = outputLogger {

        @Suppress("UNCHECKED_CAST")
        when {
            (this as? List<NetworkProfile>) != null -> this.toCliTable()
            this is String -> this
            else -> throwUnknownType()
        }
    }
}
