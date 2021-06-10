package ftl.presentation.cli.firebase

import ftl.domain.CancelLastRun
import ftl.domain.invoke
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.run.MatrixCancelStatus
import ftl.util.PrintHelpCommand
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
    PrintHelpCommand(),
    CancelLastRun {

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is MatrixCancelStatus -> mapToMessage()
            else -> throwUnknownType()
        }
    }
}
