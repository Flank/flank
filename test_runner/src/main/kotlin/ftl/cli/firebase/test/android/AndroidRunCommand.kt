package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.args.validate
import ftl.cli.firebase.test.CommonRunCommand
import ftl.config.FtlConstants
import ftl.config.emptyAndroidConfig
import ftl.mock.MockServer
import ftl.run.ANDROID_SHARD_FILE
import ftl.run.dumpShards
import ftl.run.newTestRun
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
    description = ["""Uploads the app and test apk to GCS.
Runs the espresso tests using orchestrator.
Configuration is read from flank.yml
"""],
    usageHelpAutoWidth = true
)
class AndroidRunCommand : CommonRunCommand(), Runnable {

    @CommandLine.Mixin
    override val config = emptyAndroidConfig()

    init {
        configPath = FtlConstants.defaultAndroidConfig
    }

    override fun run() {
        if (dryRun) {
            MockServer.start()
        }

        val config = AndroidArgs.load(Paths.get(configPath), cli = this).validate()
        runBlocking {
            if (dumpShards) dumpShards(args = config, obfuscatedOutput = obfuscate)
            else newTestRun(config, obfuscate)
        }
    }

    @Option(
        names = ["--dump-shards"],
        description = ["Measures test shards from given test apks and writes them into $ANDROID_SHARD_FILE file instead of executing."]
    )
    var dumpShards: Boolean = false
}
