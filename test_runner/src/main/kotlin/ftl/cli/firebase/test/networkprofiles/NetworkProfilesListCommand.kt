package ftl.cli.firebase.test.networkprofiles

import ftl.gc.GcTesting
import ftl.http.executeWithRetry
import ftl.run.common.prettyPrint
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
class NetworkProfilesListCommand : Runnable {
    override fun run() {
        println("fetching available network profiles...")
        val configurations = GcTesting.get.testEnvironmentCatalog()
            .get("NETWORK_CONFIGURATION")
            .executeWithRetry()
            ?.networkConfigurationCatalog
            ?.configurations
            ?: emptyList()
        println()
        println(prettyPrint.toJson(configurations))
    }
}
