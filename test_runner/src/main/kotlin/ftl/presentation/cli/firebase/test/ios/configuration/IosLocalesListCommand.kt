package ftl.presentation.cli.firebase.test.ios.configuration

import ftl.api.Locale
import ftl.config.FtlConstants
import ftl.domain.ListIosLocales
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.locale.toCliTable
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.asListOrNull
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print current list of locales available to test against"],
    description = ["Print current list of iOS locales available to test against"],
    usageHelpAutoWidth = true
)
class IosLocalesListCommand :
    Runnable,
    ListIosLocales {

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

    override val out = outputLogger {
        asListOrNull<Locale>()?.toCliTable() ?: throwUnknownType()
    }
}
