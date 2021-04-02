package ftl.presentation.cli.firebase.test.ios

import ftl.config.FtlConstants
import ftl.domain.DescribeIosTestEnvironment
import ftl.domain.invoke
import picocli.CommandLine

@CommandLine.Command(
    name = "test-environment",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = [
        "Print available iOS devices, OS versions list, locales, provided software, network configuration, " +
            "orientation and IP blocks to test against"
    ],
    description = [
        "Print available iOS devices, OS versions list, locales, provided software, network configuration, " +
            "orientation and IP blocks to test against"
    ],
    usageHelpAutoWidth = true
)
class IosTestEnvironmentCommand :
    Runnable,
    DescribeIosTestEnvironment {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultIosConfig

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    override fun run() = invoke()
}
