package ftl.presentation.cli.firebase.test.android

import ftl.config.FtlConstants
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.createConfiguration
import ftl.domain.RunTestAndroid
import ftl.domain.invoke
import ftl.presentation.cli.firebase.test.CommonRunCommand
import ftl.run.ANDROID_SHARD_FILE
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
        """Uploads the app and test apk to GCS.
Runs the espresso tests using orchestrator.
Configuration is read from flank.yml
"""
    ],
    usageHelpAutoWidth = true
)
class AndroidRunCommand :
    CommonRunCommand(),
    RunTestAndroid {

    @CommandLine.Mixin
    private val androidGcloudConfig = AndroidGcloudConfig()

    @CommandLine.Mixin
    private val androidFlankConfig = AndroidFlankConfig()

    override val config by createConfiguration(androidGcloudConfig, androidFlankConfig)

    @Option(
        names = ["--dump-shards"],
        description = ["Measures test shards from given test apks and writes them into $ANDROID_SHARD_FILE file instead of executing."]
    )
    override var dumpShards: Boolean = false

    init {
        configPath = FtlConstants.defaultAndroidConfig
    }

    override fun run() = invoke()
}
