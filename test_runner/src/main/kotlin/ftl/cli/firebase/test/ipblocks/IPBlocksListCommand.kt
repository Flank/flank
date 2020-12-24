package ftl.cli.firebase.test.ipblocks

import flank.common.logLn
import ftl.environment.ipBlocksListAsTable
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
class IPBlocksListCommand : Runnable {
    override fun run() {
        logLn(ipBlocksListAsTable())
    }
}
