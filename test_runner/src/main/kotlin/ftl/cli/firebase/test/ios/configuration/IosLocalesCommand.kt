package ftl.cli.firebase.test.ios.configuration

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
class IosLocalesCommand : Runnable {
    override fun run() {
        CommandLine.usage(IosLocalesCommand(), System.out)
    }
}
