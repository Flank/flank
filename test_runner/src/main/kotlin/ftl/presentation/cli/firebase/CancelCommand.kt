package ftl.presentation.cli.firebase

import ftl.domain.CancelLastRun
import ftl.domain.invoke
import picocli.CommandLine

@CommandLine.Command(
    name = "cancel",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Cancels the last Firebase Test Lab run"],
    description = [
        """Selects the most recent run in the results/ folder.
Reads in the matrix_ids.json file. Cancels any incomplete matrices.
"""
    ],
    usageHelpAutoWidth = true
)
class CancelCommand :
    Runnable,
    CancelLastRun {

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    override fun run() = invoke()
}
