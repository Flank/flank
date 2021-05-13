package ftl.presentation.cli.firebase.test.android.versions

import ftl.api.OsVersion
import ftl.config.FtlConstants
import ftl.domain.ListAndroidVersions
import ftl.domain.invoke
import ftl.environment.android.prepareDescription
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
    description = ["Print current list of Android OS versions available to test against"],
    usageHelpAutoWidth = true
)
class AndroidVersionsListCommand :
    Runnable,
    ListAndroidVersions {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig

    override val out = outputLogger {
        asListOrNull<OsVersion.Android>()?.toCliTable() ?: throwUnknownType()
    }

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    override fun run() = invoke()
}
