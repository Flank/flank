package ftl.presentation.cli.firebase.test.ios.models

import ftl.api.DeviceModel
import ftl.config.FtlConstants
import ftl.domain.ListIosModels
import ftl.domain.invoke
import ftl.environment.ios.toCliTable
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelpCommand
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
    PrintHelpCommand(),
    ListIosModels {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    override val out = outputLogger {
        when (this) {
            is DeviceModel.Ios.Available -> toCliTable()
            else -> throwUnknownType()
        }
    }

    override fun run() = invoke()
}
