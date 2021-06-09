package ftl.presentation.cli.firebase.test.android.orientations

import ftl.api.Orientation
import ftl.config.FtlConstants
import ftl.domain.ListAndroidOrientations
import ftl.domain.invoke
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelp
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
    description = ["Print current list of Android orientations available to test against"],
    usageHelpAutoWidth = true
)
class AndroidOrientationsListCommand :
    PrintHelp(),
    ListAndroidOrientations {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig

    override fun run() = invoke()

    override val out = outputLogger {
        asListOrNull<Orientation>()?.toCliTable() ?: throwUnknownType()
    }
}
