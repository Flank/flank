package ftl.presentation.cli.firebase.test.android

import ftl.presentation.cli.firebase.test.android.orientations.AndroidOrientationsListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "screen-orientations",
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
class AndroidOrientationsCommand : PrintHelp()
