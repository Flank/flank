package ftl.cli.firebase.test.ios.versions

import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.ios.IosCatalog
import ftl.run.exception.FlankConfigurationError
import picocli.CommandLine
import java.nio.file.Paths

@CommandLine.Command(
    name = "describe",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["List of OS versions available to test against"],
    description = ["Print current list of iOS versions available to test against"],
    usageHelpAutoWidth = true
)
class IosVersionsDescribeCommand : Runnable {
    override fun run() {
        if (versionId.isBlank()) throw FlankConfigurationError("Argument VERSION_ID must be specified.")
        println(IosCatalog.describeSoftwareVersion(IosArgs.loadOrDefault(Paths.get(configPath)).project, versionId))
    }

    @CommandLine.Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "VERSION_ID",
        defaultValue = "",
        description = ["The version to describe, found" +
            " using \$ gcloud firebase test ios versions list."]
    )
    var versionId: String = ""
}
