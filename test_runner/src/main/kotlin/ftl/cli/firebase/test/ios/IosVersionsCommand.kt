package ftl.cli.firebase.test.ios

import ftl.cli.firebase.test.ios.versions.IosVersionsDescribeCommand
import ftl.cli.firebase.test.ios.versions.IosVersionsListCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "versions",
    subcommands = [IosVersionsListCommand::class, IosVersionsDescribeCommand::class],
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Describe software versions"],
    description = ["Information about available software versions. For example prints list of available software versions"],
    usageHelpAutoWidth = true
)
class IosVersionsCommand : Runnable {
    override fun run() {
        CommandLine.usage(IosVersionsCommand(), System.out)
    }
}
