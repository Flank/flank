package ftl.cli.firebase.test.android.configuration

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
class AndroidLocalesCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidLocalesCommand(), System.out)
    }
}
