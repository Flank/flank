package ftl.cli.firebase.test.android

import ftl.android.AndroidCatalog.devicesCatalogAsTable
import ftl.android.AndroidCatalog.supportedVersionsAsTable
import ftl.args.AndroidArgs
import ftl.config.FtlConstants
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "test-environment",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print available devices and OS versions list to test against"],
    description = ["Print available Android devices and Android OS versions list to test against"],
    usageHelpAutoWidth = true
)
class AndroidTestEnvironmentCommand : Runnable {
    override fun run() {
        println(devicesCatalogAsTable(AndroidArgs.load(Paths.get(configPath)).project))
        println(supportedVersionsAsTable(AndroidArgs.load(Paths.get(configPath)).project))
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultAndroidConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
