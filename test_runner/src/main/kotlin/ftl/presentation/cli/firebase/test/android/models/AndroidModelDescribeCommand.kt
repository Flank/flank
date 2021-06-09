package ftl.presentation.cli.firebase.test.android.models

import ftl.api.DeviceModel
import ftl.config.FtlConstants
import ftl.domain.DescribeAndroidModels
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.android.models.describe.prepareDescription
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
    header = ["Describe android model "],
    usageHelpAutoWidth = true
)
class AndroidModelDescribeCommand :
    PrintHelp(),
    DescribeAndroidModels {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath = FtlConstants.defaultAndroidConfig

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "MODEL_ID",
        defaultValue = "",
        description = [
            "The models to describe, found" +
                " using \$ gcloud firebase test android models list."
        ]
    )
    override var modelId: String = ""

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is DeviceModel.Android -> prepareDescription()
            else -> throwUnknownType()
        }
    }
}
