package ftl.presentation.cli.firebase.test.android.orientations

import ftl.api.Orientation
import ftl.config.FtlConstants
import ftl.domain.ListAndroidOrientations
import ftl.domain.invoke
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
    header = ["List of device orientations available to test against"],
    description = ["Print current list of Android orientations available to test against"],
    usageHelpAutoWidth = true
)
class AndroidOrientationsListCommand :
    Runnable,
    ListAndroidOrientations {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    override fun run() = invoke()

    override val out = outputLogger {
        @Suppress("UNCHECKED_CAST")
        (this as? List<Orientation>)?.toCliTable() ?: throwUnknownType()
    }
}
