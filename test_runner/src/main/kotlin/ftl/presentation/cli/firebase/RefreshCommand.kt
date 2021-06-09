package ftl.presentation.cli.firebase

import ftl.domain.RefreshLastRun
import ftl.domain.invoke
import ftl.util.PrintHelpCommand
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command

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
    PrintHelpCommand(),
    RefreshLastRun {

    override fun run() = runBlocking { invoke() }
}
