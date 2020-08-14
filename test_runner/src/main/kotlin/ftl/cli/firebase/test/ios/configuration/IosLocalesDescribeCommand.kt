package ftl.cli.firebase.test.ios.configuration

import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.ios.IosCatalog.getLocaleDescription
import ftl.util.FlankConfigurationError
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "describe",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Describe a locales "],
    usageHelpAutoWidth = true
)
class IosLocalesDescribeCommand : Runnable {
    override fun run() {
        if (locale.isBlank()) throw FlankConfigurationError("Argument LOCALE must be specified.")
        print(getLocaleDescription(IosArgs.loadOrDefault(Paths.get(configPath)).project, locale))
    }

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "LOCALE",
        defaultValue = "",
        description = ["The locale to describe, found" +
                " using \$ gcloud firebase test android locales list\n."]
    )
    var locale: String = ""

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultAndroidConfig
}
