package ftl.presentation.cli.firebase.test.ios

import ftl.presentation.cli.firebase.test.ios.orientations.IosOrientationsListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "screen-orientations",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available orientation versions"],
    description = ["Prints list of available orientations"],
    subcommands = [IosOrientationsListCommand::class],
    usageHelpAutoWidth = true
)
class IosOrientationsCommand : PrintHelp()
