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

    @Option(names = ["--timeout"], description = ["""The max time this test execution can run before it is cancelled
        |(default: 15m). It does not include any time necessary to prepare and clean up the target device. The maximum
        |possible testing time is 30m on physical devices and 60m on virtual devices. The TIMEOUT units can be h, m,
        | or s. If no unit is given, seconds are assumed. """])
    var timeout: String? = null

    @Option(names = ["--async"], description = ["""Invoke a test asynchronously without waiting for test results."""])
    var async: Boolean? = null

    @Option(names = ["--project"], description = ["""The Google Cloud Platform project name to use for this invocation.
         If omitted, then the project from the service account credential is used"""])
    var project: String? = null

    @Option(names = ["--results-history-name"], description = ["""The history name for your test results
        (an arbitrary string label; default: the application's label from the APK manifest). All tests which use the
        same history name will have their results grouped together in the Firebase console in a time-ordered test
         history list."""])
    var resultsHistoryName: String? = null

    @Option(names = ["--test-shards"], description = ["""The amount of matrices to split the tests across."""])
    var testShards: Int? = null
}
