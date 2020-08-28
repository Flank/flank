package ftl.cli.firebase.test.android

import ftl.android.AndroidCatalog.devicesCatalogAsTable
import ftl.android.AndroidCatalog.localesAsTable
import ftl.android.AndroidCatalog.supportedOrientationsAsTable
import ftl.android.AndroidCatalog.supportedVersionsAsTable
import ftl.args.AndroidArgs
import ftl.args.validate
import ftl.config.FtlConstants
import ftl.environment.ipBlocksListAsTable
import ftl.environment.networkConfigurationAsTable
import ftl.environment.providedSoftwareAsTable
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "test-environment",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print available Android devices, Android OS versions list, locales, provided software, network " +
        "configuration, orientation and IP blocks to test against"],
    description = ["Print available Android devices, Android OS versions list, locales, provided software, network " +
        "configuration, orientation and IP blocks to test against"],
    usageHelpAutoWidth = true
)
class AndroidTestEnvironmentCommand : Runnable {
    override fun run() {
        val projectId = AndroidArgs.loadOrDefault(Paths.get(configPath)).validate().project
        println(devicesCatalogAsTable(projectId))
        println(supportedVersionsAsTable(projectId))
        println(localesAsTable(projectId))
        println(providedSoftwareAsTable())
        println(networkConfigurationAsTable())
        println(supportedOrientationsAsTable(projectId))
        println(ipBlocksListAsTable())
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultAndroidConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
