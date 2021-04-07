package ftl.presentation.cli.firebase.test.ios

import ftl.presentation.cli.firebase.test.ios.configuration.IosLocalesDescribeCommand
import ftl.presentation.cli.firebase.test.ios.configuration.IosLocalesListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "locales",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available locales on device"],
    description = ["Information about available locales on device. For example prints list of available locales to test against"],
    subcommands = [IosLocalesListCommand::class, IosLocalesDescribeCommand::class],
    usageHelpAutoWidth = true
)
class IosLocalesCommand : PrintHelp
