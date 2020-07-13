package ftl.cli.firebase.test.ios.versions

import picocli.CommandLine

@CommandLine.Command(
    name = "versions",
    subcommands = [IosVersionsListCommand::class],
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available software versions"],
    description = ["Information about available software versions. For example prints list of available software versions"],
    usageHelpAutoWidth = true
)
class IosVersionsCommand : Runnable {
    override fun run() {
        CommandLine.usage(IosVersionsCommand(), System.out)
    }
}
