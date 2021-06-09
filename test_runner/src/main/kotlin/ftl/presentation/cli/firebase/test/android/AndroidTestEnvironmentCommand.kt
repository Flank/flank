package ftl.presentation.cli.firebase.test.android

import ftl.api.TestEnvironment
import ftl.config.FtlConstants
import ftl.domain.DescribeAndroidTestEnvironment
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.environment.prepareOutputString
import ftl.presentation.outputLogger
import ftl.presentation.throwUnknownType
import ftl.util.PrintHelpCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "test-environment",
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = [
        "Print available Android devices, Android OS versions list, locales, provided software, network " +
            "configuration, orientation and IP blocks to test against"
    ],
    description = [
        "Print available Android devices, Android OS versions list, locales, provided software, network " +
            "configuration, orientation and IP blocks to test against"
    ],
    usageHelpAutoWidth = true
)
class AndroidTestEnvironmentCommand :
    PrintHelpCommand(),
    DescribeAndroidTestEnvironment {

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    override var configPath: String = FtlConstants.defaultAndroidConfig

    override fun run() = invoke()

    override val out = outputLogger {
        when (this) {
            is TestEnvironment.Android -> prepareOutputString()
            else -> throwUnknownType()
        }
    }
}
