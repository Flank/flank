package ftl.presentation.cli.firebase.test.android.locales

import ftl.api.Locale
import ftl.config.FtlConstants
import ftl.domain.DescribeAndroidLocales
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.locale.prepareDescription
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
    header = ["Describe a locales "],
    usageHelpAutoWidth = true
)
class AndroidLocalesDescribeCommand :
    PrintHelp(),
    DescribeAndroidLocales {

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "LOCALE",
        defaultValue = "",
        description = [
            "The locale to describe, found" +
                " using \$ gcloud firebase test android locales list\n."
        ]
    )
    override var locale: String = ""

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig

    override val out = outputLogger {
        when (this) {
            is Locale -> prepareDescription()
            else -> throwUnknownType()
        }
    }

    override fun run() = invoke()
}
