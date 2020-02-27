package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.args.AndroidTestShard
import ftl.args.yml.AppTestPair
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.config.FtlConstants.defaultLocale
import ftl.config.FtlConstants.defaultOrientation
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
    description = ["""Uploads the app and test apk to GCS.
Runs the espresso tests using orchestrator.
Configuration is read from flank.yml
"""],
    usageHelpAutoWidth = true
)
class AndroidRunCommand : Runnable {

    override fun run() {
        if (dryRun) {
            MockServer.start()
        }

        val config = AndroidArgs.load(Paths.get(configPath), cli = this)

        if (dumpShards) {
            val testShardChunks = AndroidTestShard.getTestShardChunks(config, config.testApk)
            val testShardChunksJson = TestRunner.gson.toJson(testShardChunks)

            Files.write(Paths.get(shardFile), testShardChunksJson.toByteArray())
            println("Saved shards to $shardFile")
            exitProcess(0)
        }

        runBlocking {
            TestRunner.newRun(config)
        }
    }

    companion object {
        private const val shardFile = "android_shards.json"
    }

    // Flank debug

    @Option(names = ["--dump-shards"], description = ["Dumps the shards to $shardFile for debugging"])
    var dumpShards: Boolean = false

    @Option(names = ["--dry"], description = ["Dry run on mock server"])
    var dryRun: Boolean = false

    // Flank specific

    @Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var configPath: String = FtlConstants.defaultAndroidConfig

    @Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    // AndroidGcloudYml.kt

    @Option(
        names = ["--app"],
        description = ["The path to the application binary file. " +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation."]
    )
    var app: String? = null

    @Option(
        names = ["--test"],
        description = ["The path to the binary file containing instrumentation tests. " +
                "The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."]
    )
    var test: String? = null

    @Option(
        names = ["--auto-google-login"],
        description = ["Automatically log into the test device using a preconfigured " +
                "Google account before beginning the test. Enabled by default, use --no-auto-google-login to disable."]
    )
    var autoGoogleLogin: Boolean? = null

    @Option(
        names = ["--no-auto-google-login"],
        description = ["Google account not logged in. See --auto-google-login."]
    )
    var noAutoGoogleLogin: Boolean? = null

    @Option(
        names = ["--use-orchestrator"],
        description = ["Whether each test runs in its own Instrumentation instance " +
                "with the Android Test Orchestrator (default: Orchestrator is used. To disable, use --no-use-orchestrator). " +
                "Orchestrator is only compatible with AndroidJUnitRunner v1.0 or higher. See " +
                "https://developer.android.com/training/testing/junit-runner.html#using-android-test-orchestrator for more " +
                "information about Android Test Orchestrator."]
    )
    var useOrchestrator: Boolean? = null

    @Option(
        names = ["--no-use-orchestrator"],
        description = ["Orchestrator is not used. See --use-orchestrator."]
    )
    var noUseOrchestrator: Boolean? = null

    @Option(
        names = ["--environment-variables"],
        split = ",",
        description = ["A comma-separated, key=value map of environment variables " +
                "and their desired values. --environment-variables=coverage=true,coverageFile=/sdcard/coverage.ec " +
                "The environment variables are mirrored as extra options to the am instrument -e KEY1 VALUE1 … command and " +
                "passed to your test runner (typically AndroidJUnitRunner)"]
    )
    var environmentVariables: Map<String, String>? = null

    @Option(
        names = ["--directories-to-pull"],
        split = ",",
        description = ["A list of paths that will be copied from the device's " +
                "storage to the designated results bucket after the test is complete. These must be absolute paths under " +
                "/sdcard or /data/local/tmp (for example, --directories-to-pull /sdcard/tempDir1,/data/local/tmp/tempDir2). " +
                "Path names are restricted to the characters a-zA-Z0-9_-./+. The paths /sdcard and /data will be made available " +
                "and treated as implicit path substitutions. E.g. if /sdcard on a particular device does not map to external " +
                "storage, the system will replace it with the external storage path prefix for that device."]
    )
    var directoriesToPull: List<String>? = null

    @Option(
        names = ["--performance-metrics"],
        description = ["Monitor and record performance metrics: CPU, memory, " +
                "network usage, and FPS (game-loop only). Enabled by default, use --no-performance-metrics to disable."]
    )
    var performanceMetrics: Boolean? = null

    @Option(
        names = ["--no-performance-metrics"],
        description = ["Disables performance metrics. See --performance-metrics"]
    )
    var noPerformanceMetrics: Boolean? = null

    @Option(
        names = ["--test-runner-class"],
        description = ["The fully-qualified Java class name of the instrumentation test runner (default: the last name extracted " +
                "from the APK manifest)."]
    )
    var testRunnerClass: String? = null

    @Option(
        names = ["--test-targets"],
        split = ",",
        description = ["A list of one or more test target filters to apply " +
                "(default: run all test targets). Each target filter must be fully qualified with the package name, class name, " +
                "or test annotation desired. Any test filter supported by am instrument -e … is supported. " +
                "See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more " +
                "information."]
    )
    var testTargets: List<String>? = null

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
            model = map.getOrDefault("model", defaultAndroidModel),
            version = map.getOrDefault("version", defaultAndroidVersion),
            locale = map.getOrDefault("locale", defaultLocale),
            orientation = map.getOrDefault("orientation", defaultOrientation)
        )

        if (device == null) device = mutableListOf()
        device?.add(androidDevice)
    }

    var device: MutableList<Device>? = null

    // GcloudYml.kt

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
        description = [
            "A list of one or more test methods to always run first in every shard."]
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
    var localResultDir: String? = null

    // AndroidFlankYml

    @Option(
        names = ["--additional-app-test-apks"],
        split = ",",
        description = ["A list of app & test apks to include in the run. " +
                "Useful for running multiple module tests within a single Flank run."]
    )
    fun apkMap(map: Map<String, String>?) {
        if (map.isNullOrEmpty()) return
        if (additionalAppTestApks == null) additionalAppTestApks = mutableListOf()

        val appApk = map["app"]
        val testApk = map["test"]

        if (appApk != null && testApk != null) {
            additionalAppTestApks?.add(
                AppTestPair(
                    app = appApk,
                    test = testApk
                )
            )
        }
    }

    var additionalAppTestApks: MutableList<AppTestPair>? = null

    @Option(
            names = ["--keep-file-path"],
            description = ["Keeps the full path of downloaded files. " +
                    "Required when file names are not unique."]
    )
    var keepFilePath: Boolean? = null
}
