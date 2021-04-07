package ftl.presentation.cli.firebase.test.networkprofiles

import ftl.domain.DescribeNetworkProfiles
import ftl.domain.invoke
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
    Runnable,
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
}
