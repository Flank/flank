package ftl.cli.firebase.test.ios.models

import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.ios.IosCatalog.devicesCatalogAsTable
import flank.common.logLn
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print current list of devices available to test against"],
    description = ["Print current list of iOS devices available to test against"],
    usageHelpAutoWidth = true
)
class IosModelsListCommand : Runnable {
    override fun run() {
        logLn(devicesCatalogAsTable(IosArgs.loadOrDefault(Paths.get(configPath)).project))
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
