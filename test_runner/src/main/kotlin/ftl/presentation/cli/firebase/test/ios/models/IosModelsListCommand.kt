package ftl.presentation.cli.firebase.test.ios.models

import ftl.config.FtlConstants
import ftl.domain.ListIosModels
import ftl.domain.invoke
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print current list of devices available to test against"],
    description = ["Print current list of iOS devices available to test against"],
    usageHelpAutoWidth = true
)
class IosModelsListCommand :
    Runnable,
    ListIosModels {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    override fun run() = invoke()
}
