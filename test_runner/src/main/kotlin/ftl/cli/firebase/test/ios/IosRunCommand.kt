package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.args.validate
import ftl.cli.firebase.test.CommonRunCommand
import ftl.config.FtlConstants
import ftl.config.emptyIosConfig
import ftl.mock.MockServer
import ftl.run.IOS_SHARD_FILE
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
    description = ["""Uploads the app and tests to GCS.
Runs the XCTests and XCUITests.
Configuration is read from flank.yml
"""],
    usageHelpAutoWidth = true
)
class IosRunCommand : CommonRunCommand(), Runnable {

    @CommandLine.Mixin
    override val config = emptyIosConfig()

    init {
        configPath = FtlConstants.defaultIosConfig
    }

    override fun run() {
        if (dryRun) {
            MockServer.start()
        }

        val config = IosArgs.load(Paths.get(configPath), cli = this).validate()

        if (dumpShards) {
            dumpShards(args = config)
        } else runBlocking {
            newTestRun(config)
        }
    }

    @Option(
        names = ["--dump-shards"],
        description = ["Measures test shards from given test apks and writes them into $IOS_SHARD_FILE file instead of executing."]
    )
    var dumpShards: Boolean = false
}
