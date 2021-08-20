package ftl.presentation.cli.firebase.test.refresh

import ftl.domain.RefreshLastRun
import ftl.domain.RefreshLastRunState
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.reportmanager.ReportManagerState
import ftl.presentation.cli.firebase.test.reportmanager.handleReportManagerState
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelpCommand
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

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is RefreshLastRunState -> handleRefreshLastRunState(this)
            is ReportManagerState -> handleReportManagerState(this)
            // TODO #2136 handle uploading file here
            else -> throwUnknownType()
        }
    }
}
