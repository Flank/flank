package ftl.presentation.cli.firebase.test.android

import ftl.presentation.cli.firebase.test.android.versions.AndroidVersionsDescribeCommand
import ftl.presentation.cli.firebase.test.android.versions.AndroidVersionsListCommand
import ftl.util.PrintHelpCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "versions",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available software versions"],
    description = ["Information about available software versions. For example prints list of available software versions"],
    subcommands = [AndroidVersionsListCommand::class, AndroidVersionsDescribeCommand::class],
    usageHelpAutoWidth = true
)
class AndroidVersionsCommand : PrintHelpCommand()
