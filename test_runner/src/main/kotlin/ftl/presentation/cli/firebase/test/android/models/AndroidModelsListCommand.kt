package ftl.presentation.cli.firebase.test.android.models

import ftl.api.DeviceModel
import ftl.config.FtlConstants
import ftl.domain.ListAndroidModels
import ftl.domain.invoke
import ftl.environment.android.toCliTable
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelp
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print current list of devices available to test against"],
    description = ["Print current list of Android devices available to test against"],
    usageHelpAutoWidth = true
)
class AndroidModelsListCommand :
    PrintHelp(),
    ListAndroidModels {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig
    override val out = outputLogger {
        when (this) {
            is DeviceModel.Android.Available -> toCliTable()
            else -> throwUnknownType()
        }
    }

    override fun run() = invoke()
}
