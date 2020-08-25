package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.environment.ipBlocksListAsTable
import ftl.environment.networkConfigurationAsTable
import ftl.environment.providedSoftwareAsTable
import ftl.ios.IosCatalog.devicesCatalogAsTable
import ftl.ios.IosCatalog.localesAsTable
import ftl.ios.IosCatalog.softwareVersionsAsTable
import ftl.ios.IosCatalog.supportedOrientationsAsTable
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "test-environment",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print available iOS devices, OS versions list, locales, provided software, network configuration, " +
        "orientation and IP blocks to test against"],
    description = ["Print available iOS devices, OS versions list, locales, provided software, network configuration, " +
        "orientation and IP blocks to test against"],
    usageHelpAutoWidth = true
)
class IosTestEnvironmentCommand : Runnable {
    override fun run() {
        val projectId = IosArgs.loadOrDefault(Paths.get(configPath)).project
        println(devicesCatalogAsTable(projectId))
        println(softwareVersionsAsTable(projectId))
        println(localesAsTable(projectId))
        println(providedSoftwareAsTable())
        println(networkConfigurationAsTable())
        println(supportedOrientationsAsTable(projectId))
        println(ipBlocksListAsTable())
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
