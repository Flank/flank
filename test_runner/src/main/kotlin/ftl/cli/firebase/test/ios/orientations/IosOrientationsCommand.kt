package ftl.cli.firebase.test.ios.orientations

import picocli.CommandLine

@CommandLine.Command(
    name = "orientations",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available orientation versions"],
    description = ["Information about available orientation versions. For example prints list of available orientation versions"],
    subcommands = [IosOrientationsListCommand::class],
    usageHelpAutoWidth = true
)
class IosOrientationsCommand : Runnable {
    override fun run() {
        CommandLine.usage(IosOrientationsCommand(), System.out)
    }
}
