package ftl.presentation.cli.firebase.test.ios.orientations

import ftl.api.Orientation
import ftl.config.FtlConstants
import ftl.domain.ListIosOrientations
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.android.orientations.toCliTable
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelpCommand
import ftl.util.asListOrNull
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["List of device orientations available to test against"],
    description = ["Print current list of iOS orientations available to test against"],
    usageHelpAutoWidth = true
)
class IosOrientationsListCommand :
    PrintHelpCommand(),
    ListIosOrientations {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    override fun run() = invoke()

    override val out = outputLogger {
        asListOrNull<Orientation>()?.toCliTable() ?: throwUnknownType()
    }
}
