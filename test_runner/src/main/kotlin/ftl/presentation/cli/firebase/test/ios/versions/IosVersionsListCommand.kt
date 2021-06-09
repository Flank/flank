package ftl.presentation.cli.firebase.test.ios.versions

import ftl.api.OsVersion
import ftl.config.FtlConstants
import ftl.domain.ListIosVersions
import ftl.domain.invoke
import ftl.environment.ios.toCliTable
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
    header = ["List of OS versions available to test against"],
    description = ["Print current list of iOS versions available to test against"],
    usageHelpAutoWidth = true
)
class IosVersionsListCommand :
    PrintHelp(),
    ListIosVersions {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is OsVersion.Ios.Available -> toCliTable()
            else -> throwUnknownType()
        }
    }
}
