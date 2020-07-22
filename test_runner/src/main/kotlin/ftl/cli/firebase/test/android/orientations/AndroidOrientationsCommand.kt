package ftl.cli.firebase.test.android.orientations

import picocli.CommandLine

@CommandLine.Command(
    name = "orientations",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available orientations"],
    description = ["Prints list of available orientations"],
    subcommands = [AndroidOrientationsListCommand::class],
    usageHelpAutoWidth = true
)
class AndroidOrientationsCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidOrientationsCommand(), System.out)
    }
}
