package ftl.cli.firebase.test.android

import ftl.cli.firebase.test.android.versions.AndroidVersionsDescribeCommand
import ftl.cli.firebase.test.android.versions.AndroidVersionsListCommand
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
class AndroidVersionsCommand : Runnable {
    override fun run() {
        CommandLine.usage(AndroidVersionsCommand(), System.out)
    }
}
