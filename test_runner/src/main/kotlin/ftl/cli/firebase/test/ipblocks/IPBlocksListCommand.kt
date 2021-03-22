package ftl.cli.firebase.test.ipblocks

import ftl.domain.ListIPBlocks
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
    header = ["List all IP address blocks used by Firebase Test Lab devices"],
    usageHelpAutoWidth = true
)
class IPBlocksListCommand :
    Runnable,
    ListIPBlocks {
    override fun run() = invoke()
}
