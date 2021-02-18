package ftl.cli.firebase.test.android

import flank.common.logLn
import ftl.analytics.sendConfiguration
import ftl.args.AndroidArgs
import ftl.args.setupLogLevel
import ftl.args.validate
import ftl.cli.firebase.test.CommonRunCommand
import ftl.config.FtlConstants
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.createConfiguration
import ftl.mock.MockServer
import ftl.reports.output.configure
import ftl.reports.output.log
import ftl.reports.output.outputReport
import ftl.reports.output.toOutputReportConfiguration
import ftl.run.ANDROID_SHARD_FILE
import ftl.run.dumpShards
import ftl.run.newTestRun
import ftl.util.DEVICE_SYSTEM
import ftl.util.TEST_TYPE
import ftl.util.printVersionInfo
import ftl.util.setCrashReportTag
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Paths

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
class AndroidRunCommand : CommonRunCommand(), Runnable {

    @CommandLine.Mixin
    private val androidGcloudConfig = AndroidGcloudConfig()

    @CommandLine.Mixin
    private val androidFlankConfig = AndroidFlankConfig()

    override val config by createConfiguration(androidGcloudConfig, androidFlankConfig)

    init {
        configPath = FtlConstants.defaultAndroidConfig
    }

    override fun run() {
        printVersionInfo()

        if (dryRun) {
            MockServer.start()
        }

        AndroidArgs.load(Paths.get(configPath), cli = this).apply {
            setupLogLevel()

            outputReport.configure(toOutputReportConfiguration())
            outputReport.log(this)
            setCrashReportTag(
                DEVICE_SYSTEM to "android",
                TEST_TYPE to type?.name.orEmpty()
            )
            sendConfiguration()
        }.validate().also { args ->
            runBlocking {
                if (dumpShards)
                    args.dumpShards()
                else {
                    logLn(args)
                    args.newTestRun()
                }
            }
        }
    }

    @Option(
        names = ["--dump-shards"],
        description = ["Measures test shards from given test apks and writes them into $ANDROID_SHARD_FILE file instead of executing."]
    )
    var dumpShards: Boolean = false
}
