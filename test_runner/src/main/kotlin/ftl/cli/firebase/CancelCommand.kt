package ftl.cli.firebase

import ftl.args.AndroidArgs
import ftl.run.TestRunner
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
    description = ["""Selects the most recent run in the results/ folder.
Reads in the matrix_ids.json file. Cancels any incomplete matrices.
"""],
    usageHelpAutoWidth = true
)
class CancelCommand : Runnable {
    override fun run() {
        TestRunner.cancelLastRun(AndroidArgs.default())
    }

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
