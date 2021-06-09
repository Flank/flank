package ftl.presentation.cli.firebase.test.ios.models

import ftl.api.DeviceModel
import ftl.config.FtlConstants
import ftl.domain.DescribeIosModels
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.ios.models.describe.prepareDescription
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "describe",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Describe iOS model "],
    usageHelpAutoWidth = true
)
class IosModelDescribeCommand :
    PrintHelp(),
    DescribeIosModels {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "MODEL_ID",
        defaultValue = "",
        description = [
            "The models to describe, found" +
                " using \$ gcloud firebase test ios models list."
        ]
    )
    override var modelId: String = ""

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is DeviceModel.Ios -> prepareDescription()
            else -> throwUnknownType()
        }
    }
}
