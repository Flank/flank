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

    @Option(names = ["--repeat-tests"], description = ["""The amount of times to repeat the test executions."""])
    var repeatTests: Int? = null

    @Option(names = ["--test-targets-always-run"], split = ",", description = ["""A list of one or more test methods to always run
        |first in every shard."""])
    var testTargetsAlwaysRun: List<String>? = null

    @Option(names = ["--test-targets"], split = ",", description = ["""A list of one or more test method
        names to run (default: run all test targets)."""])
    var testTargets: List<String>? = null

    @Option(names = ["--test"], description = ["""The path to the test package (a zip file containing the iOS app
        | and XCTest files). The given path may be in the local filesystem or in Google Cloud Storage using a URL
        |  beginning with gs://. Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file
        |   is specified."""])
    var test: String? = null

    @Option(names = ["--xctestrun-file"], description = ["""The path to an .xctestrun file that will override any
        |.xctestrun file contained in the --test package. Because the .xctestrun file contains environment variables
        |along with test methods to run and/or ignore, this can be useful for customizing or sharding test suites. The
        | given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."""])
    var xctestrunFile: String? = null
}
