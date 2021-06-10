package ftl.presentation.cli.firebase.test.android

import ftl.presentation.cli.firebase.test.android.locales.AndroidLocalesDescribeCommand
import ftl.presentation.cli.firebase.test.android.locales.AndroidLocalesListCommand
import ftl.util.PrintHelpCommand
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
    subcommands = [AndroidLocalesListCommand::class, AndroidLocalesDescribeCommand::class],
    usageHelpAutoWidth = true
)
class AndroidLocalesCommand : PrintHelpCommand()
