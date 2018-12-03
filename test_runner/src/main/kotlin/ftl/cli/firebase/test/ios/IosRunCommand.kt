package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.run.TestRunner
import kotlinx.coroutines.runBlocking
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
"""]
)
class IosRunCommand : Runnable {
    override fun run() {
        val config = IosArgs.load(Paths.get(configPath))
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = FtlConstants.defaultIosConfig

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    @Option(names = ["--results-bucket"], description = ["""The name of a Google Cloud Storage bucket where raw test
        |results will be stored (default: "test-lab-<random-UUID>"). Note that the bucket must be owned by a
        |billing-enabled project, and that using a non-default bucket will result in billing charges for the
        |storage used."""])
    var resultsBucket: String? = null

    @Option(names = ["--record-video"], description = ["""Enable video recording during the test.
        |Enabled by default, use --no-record-video to disable."""])
    var recordVideo: Boolean? = null

    @Option(names = ["--no-record-video"], description = ["""Disable video recording during the test. See --record-video to enable."""])
    var noRecordVideo: Boolean? = null
}
