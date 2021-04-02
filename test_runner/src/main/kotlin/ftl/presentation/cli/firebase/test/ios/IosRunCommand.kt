package ftl.presentation.cli.firebase.test.ios

import ftl.config.FtlConstants
import ftl.config.createConfiguration
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import ftl.domain.RunIosTest
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.CommonRunCommand
import ftl.run.IOS_SHARD_FILE
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(
    name = "run",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Run tests on Firebase Test Lab"],
    description = [
        """Uploads the app and tests to GCS.
Runs the XCTests and XCUITests.
Configuration is read from flank.yml
"""
    ],
    usageHelpAutoWidth = true
)
class IosRunCommand :
    CommonRunCommand(),
    RunIosTest {

    @CommandLine.Mixin
    private val iosGcloudConfig = IosGcloudConfig()

    @CommandLine.Mixin
    private val iosFlankConfig = IosFlankConfig()

    override val config by createConfiguration(iosGcloudConfig, iosFlankConfig)

    @Option(
        names = ["--dump-shards"],
        description = ["Measures test shards from given test apks and writes them into $IOS_SHARD_FILE file instead of executing."]
    )
    override var dumpShards: Boolean = false

    init {
        configPath = FtlConstants.defaultIosConfig
    }

    override fun run() = invoke()
}
