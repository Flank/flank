package ftl.cli.firebase.test.ios.configuration

import flank.common.logLn
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.ios.IosCatalog.localesAsTable
import java.nio.file.Paths
import picocli.CommandLine

@CommandLine.Command(
    name = "list",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Print current list of locales available to test against"],
    description = ["Print current list of iOS locales available to test against"],
    usageHelpAutoWidth = true
)
class IosLocalesListCommand : Runnable {
    override fun run() {
        logLn(localesAsTable(projectId = IosArgs.loadOrDefault(Paths.get(configPath)).project))
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
