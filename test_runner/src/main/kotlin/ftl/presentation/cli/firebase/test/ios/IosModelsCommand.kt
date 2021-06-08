package ftl.presentation.cli.firebase.test.ios

import ftl.presentation.cli.firebase.test.ios.models.IosModelDescribeCommand
import ftl.presentation.cli.firebase.test.ios.models.IosModelsListCommand
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "models",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Information about available models"],
    description = ["Information about available models. For example prints list of available models to test against"],
    subcommands = [IosModelsListCommand::class, IosModelDescribeCommand::class],
    usageHelpAutoWidth = true
)
class IosModelsCommand : PrintHelp {
    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false
}
