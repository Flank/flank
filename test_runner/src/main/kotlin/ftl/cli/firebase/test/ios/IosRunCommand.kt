package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.mock.MockServer
import ftl.run.TestRunner
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess
import kotlinx.coroutines.runBlocking
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
    description = ["""Uploads the app and tests to GCS.
Runs the XCTests and XCUITests.
Configuration is read from flank.yml
"""],
    usageHelpAutoWidth = true
)
class IosRunCommand : Runnable {
    override fun run() {
        if (dryRun) {
            MockServer.start()
        }

        val config = IosArgs.load(Paths.get(configPath), cli = this)

        if (dumpShards) {
            val testShardChunksJson = TestRunner.gson.toJson(config.testShardChunks)
            Files.write(Paths.get(shardFile), testShardChunksJson.toByteArray())
            println("Saved shards to $shardFile")
            exitProcess(0)
        }

        runBlocking {
            TestRunner.newRun(config)
        }
    }

    companion object {
        private const val shardFile = "ios_shards.json"
    }

    // Flank debug

    @Option(names = ["--dump-shards"], description = ["Dumps the shards to ios_shards.json for debugging"])
    var dumpShards: Boolean = false

    @Option(names = ["--dry"], description = ["Dry run on mock server"])
    var dryRun: Boolean = false

    // Flank specific

    @Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var configPath: String = FtlConstants.defaultIosConfig

    @Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    // IosGcloudYml.kt

    @Option(
        names = ["--test"],
        description = ["The path to the test package (a zip file containing the iOS app " +
                "and XCTest files). The given path may be in the local filesystem or in Google Cloud Storage using a URL " +
                "beginning with gs://. Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file " +
                "is specified."]
    )
    var test: String? = null

    @Option(
        names = ["--xctestrun-file"],
        description = ["The path to an .xctestrun file that will override any " +
                ".xctestrun file contained in the --test package. Because the .xctestrun file contains environment variables " +
                "along with test methods to run and/or ignore, this can be useful for customizing or sharding test suites. The " +
                "given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."]
    )
    var xctestrunFile: String? = null

    @Option(
        names = ["--xcode-version"],
        description = ["The version of Xcode that should be used to run an XCTest. " +
                "Defaults to the latest Xcode version supported in Firebase Test Lab. This Xcode version must be supported by " +
                "all iOS versions selected in the test matrix."]
    )
    var xcodeVersion: String? = null

    @Option(
        names = ["--device"],
        split = ",",
        description = ["A list of DIMENSION=VALUE pairs which specify a target " +
                "device to test against. This flag may be repeated to specify multiple devices. The four device dimensions are: " +
                "model, version, locale, and orientation. If any dimensions are omitted, they will use a default value. Omitting " +
                "all of the preceding dimension-related flags will run tests against a single device using defaults for all four " +
                "device dimensions."]
    )
    fun deviceMap(map: Map<String, String>?) {
        if (map.isNullOrEmpty()) return
        val androidDevice = Device(
            model = map.getOrDefault("model", defaultIosModel),
            version = map.getOrDefault("version", defaultIosVersion),
            locale = map.getOrDefault("locale", FtlConstants.defaultLocale),
            orientation = map.getOrDefault("orientation", FtlConstants.defaultOrientation)
        )

        if (device == null) device = mutableListOf()
        device?.add(androidDevice)
    }

    var device: MutableList<Device>? = null

    // GcloudYml

    @Option(
        names = ["--results-bucket"],
        description = ["The name of a Google Cloud Storage bucket where raw test " +
            "results will be stored (default: \"test-lab-<random-UUID>\"). Note that the bucket must be owned by a " +
            "billing-enabled project, and that using a non-default bucket will result in billing charges for the " +
            "storage used."]
    )
    var resultsBucket: String? = null

    @Option(
        names = ["--results-dir"],
        description = [
            "The name of a unique Google Cloud Storage object within the results bucket where raw test results will be " +
                    "stored (default: a timestamp with a random suffix). Caution: if specified, this argument must be unique for " +
                    "each test matrix you create, otherwise results from multiple test matrices will be overwritten or " +
                    "intermingled."]
    )
    var resultsDir: String? = null

    @Option(
        names = ["--record-video"],
        description = ["Enable video recording during the test. " +
            "Enabled by default, use --no-record-video to disable."]
    )
    var recordVideo: Boolean? = null

    @Option(
        names = ["--no-record-video"],
        description = ["Disable video recording during the test. See --record-video to enable."]
    )
    var noRecordVideo: Boolean? = null

    @Option(
        names = ["--timeout"],
        description = ["The max time this test execution can run before it is cancelled " +
            "(default: 15m). It does not include any time necessary to prepare and clean up the target device. The maximum " +
            "possible testing time is 30m on physical devices and 60m on virtual devices. The TIMEOUT units can be h, m, " +
            "or s. If no unit is given, seconds are assumed. "]
    )
    var timeout: String? = null

    @Option(
        names = ["--async"],
        description = ["Invoke a test asynchronously without waiting for test results."]
    )
    var async: Boolean? = null

    @Option(
        names = ["--results-history-name"],
        description = ["The history name for your test results " +
            "(an arbitrary string label; default: the application's label from the APK manifest). All tests which use the " +
            "same history name will have their results grouped together in the Firebase console in a time-ordered test " +
            "history list."]
    )
    var resultsHistoryName: String? = null

    @Option(
        names = ["--num-flaky-test-attempts"],
        description = ["The number of times a TestExecution should be re-attempted if one or more of its test cases " +
                "fail for any reason. The maximum number of reruns allowed is 10. Default is 0, which implies no reruns."]
    )
    var flakyTestAttempts: Int? = null

    // FlankYml.kt
    @Option(
        names = ["--max-test-shards"],
        description = ["The amount of matrices to split the tests across."]
    )
    var maxTestShards: Int? = null

    @Option(
        names = ["--shard-time"],
        description = ["The max amount of seconds each shard should run."]
    )
    var shardTime: Int? = null

    @Option(
        names = ["--num-test-runs"],
        description = ["The amount of times to run the test executions."]
    )
    var repeatTests: Int? = null

    @Option(
        names = ["--smart-flank-gcs-path"],
        description = ["Google cloud storage path to save test timing data used by smart flank."]
    )
    var smartFlankGcsPath: String? = null

    @Option(
        names = ["--smart-flank-disable-upload"],
        description = ["Disables smart flank JUnit XML uploading. Useful for preventing timing data from being updated."]
    )
    var smartFlankDisableUpload: Boolean? = null

    @Option(
        names = ["--disable-sharding"],
        description = ["Disable sharding."]
    )
    var disableSharding: Boolean? = null

    @Option(
        names = ["--test-targets-always-run"],
        split = ",",
        description = ["A list of one or more test methods to always run first in every shard."]
    )
    var testTargetsAlwaysRun: List<String>? = null

    @Option(
        names = ["--files-to-download"],
        split = ",",
        description = ["A list of paths that will be downloaded from the resulting bucket " +
                "to the local results folder after the test is complete. These must be absolute paths " +
                "(for example, --files-to-download /images/tempDir1,/data/local/tmp/tempDir2). " +
                "Path names are restricted to the characters a-zA-Z0-9_-./+."]
    )
    var filesToDownload: List<String>? = null

    @Option(
        names = ["--project"],
        description = ["The Google Cloud Platform project name to use for this invocation. " +
                "If omitted, then the project from the service account credential is used"]
    )
    var project: String? = null

    @Option(
        names = ["--local-result-dir"],
        description = ["Saves test result to this local folder. Deleted before each run."]
    )
    var localResultsDir: String? = null

    // IosFlankYml.kt

    @Option(
        names = ["--test-targets"],
        split = ",",
        description = ["A list of one or more test method " +
                "names to run (default: run all test targets)."]
    )
    var testTargets: List<String>? = null
}
