package ftl.cli.firebase.test.android.models

import ftl.android.AndroidCatalog
import ftl.args.AndroidArgs
import ftl.config.FtlConstants
import ftl.run.exception.FlankConfigurationError
import picocli.CommandLine
import java.nio.file.Paths

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
class AndroidModelDescribeCommand : Runnable {
    override fun run() {
        if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
        println(AndroidCatalog.describeModel(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, modelId))
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultAndroidConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "MODEL_ID",
        defaultValue = "",
        description = ["The models to describe, found" +
            " using \$ gcloud firebase test android models list."]
    )
    var modelId: String = ""
}
