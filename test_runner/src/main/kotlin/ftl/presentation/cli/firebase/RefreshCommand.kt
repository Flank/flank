package ftl.presentation.cli.firebase

import ftl.domain.RefreshLastRun
import ftl.domain.invoke
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "refresh",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Downloads results for the last Firebase Test Lab run"],
    description = [
        """Selects the most recent run in the results/ folder.
Reads in the matrix_ids.json file. Refreshes any incomplete matrices.
"""
    ],
    usageHelpAutoWidth = true
)
class RefreshCommand :
    Runnable,
    RefreshLastRun {

    @Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    override fun run() = runBlocking { invoke() }
}
