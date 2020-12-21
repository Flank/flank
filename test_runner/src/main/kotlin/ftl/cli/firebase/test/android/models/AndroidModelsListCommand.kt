package ftl.cli.firebase.test.android.models

import ftl.android.AndroidCatalog
import ftl.args.AndroidArgs
import ftl.config.FtlConstants
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
    description = ["Print current list of Android devices available to test against"],
    usageHelpAutoWidth = true
)
class AndroidModelsListCommand : Runnable {
    override fun run() {
        logLn(AndroidCatalog.devicesCatalogAsTable(AndroidArgs.loadOrDefault(Paths.get(configPath)).project))
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultAndroidConfig

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
