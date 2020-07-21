package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.environment.providedSoftwareAsTable
import ftl.environment.networkConfigurationAsTable
import ftl.ios.IosCatalog.devicesCatalogAsTable
import ftl.ios.IosCatalog.softwareVersionsAsTable
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "test-environment",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print available devices, OS versions, provided software list and network configuration to test against"],
    description = ["Print available iOS devices, iOS OS versions list, provided software and network configuration to test against"],
    usageHelpAutoWidth = true
)
class IosTestEnvironmentCommand : Runnable {
    override fun run() {
        println(devicesCatalogAsTable(IosArgs.load(Paths.get(configPath)).project))
        println(softwareVersionsAsTable(IosArgs.load(Paths.get(configPath)).project))
        println(providedSoftwareAsTable())
        println(networkConfigurationAsTable())
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
