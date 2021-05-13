package ftl.presentation.cli.firebase.test.ios.versions

import ftl.api.OsVersion
import ftl.config.FtlConstants
import ftl.domain.ListIosVersions
import ftl.domain.invoke
import ftl.environment.ios.toCliTable
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["List of OS versions available to test against"],
    description = ["Print current list of iOS versions available to test against"],
    usageHelpAutoWidth = true
)
class IosVersionsListCommand :
    Runnable,
    ListIosVersions {

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
        when (this) {
            is OsVersion.Ios.Available -> toCliTable()
            else -> throwUnknownType()
        }
    }
}
