package ftl.presentation.cli.firebase.test.networkprofiles

import ftl.api.NetworkProfile
import ftl.domain.DescribeNetworkProfiles
import ftl.domain.invoke
import ftl.environment.prepareDescription
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "describe",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Describe a network profile "],
    usageHelpAutoWidth = true
)
class NetworkProfilesDescribeCommand :
    PrintHelp(),
    DescribeNetworkProfiles {

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "PROFILE_ID",
        defaultValue = "",
        description = [
            "The network profile to describe, found" +
                " using \$ gcloud beta firebase test network-profiles list."
        ]
    )
    override var profileId: String = ""

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is NetworkProfile -> prepareDescription()
            is NetworkProfile.ErrorMessage -> message
            else -> throwUnknownType()
        }
    }
}
