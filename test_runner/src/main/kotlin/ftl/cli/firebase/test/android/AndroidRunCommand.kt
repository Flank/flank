package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.args.AndroidTestShard
import ftl.args.ShardChunks
import ftl.args.yml.AppTestPair
import ftl.cli.firebase.test.CommonRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.config.FtlConstants.defaultLocale
import ftl.config.FtlConstants.defaultOrientation
import ftl.mock.MockServer
import ftl.run.common.prettyPrint
import ftl.run.newTestRun
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Files
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

    override fun run() {
        if (dryRun) {
            MockServer.start()
        }

        val config = AndroidArgs.load(Paths.get(configPath), cli = this)

        if (dumpShards) {
            val testShardChunks: ShardChunks = AndroidTestShard.getAllLocalTestShardChunks(config)
            val testShardChunksJson: String = prettyPrint.toJson(testShardChunks)

            Files.write(Paths.get(shardFile), testShardChunksJson.toByteArray())
            println("Saved ${testShardChunks.size} shards to $shardFile")
        } else {
            runBlocking {
                newTestRun(config)
            }
        }
    }

    companion object {
        private const val shardFile = "android_shards.json"
    }

    // Flank debug

    @Option(names = ["--dump-shards"], description = ["Dumps the shards to $shardFile for debugging"])
    var dumpShards: Boolean = false

    // Flank specific

    @Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var configPath: String = FtlConstants.defaultAndroidConfig

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
        names = ["--additional-apks"],
        split = ",",
        description = ["A list of up to 100 additional APKs to install, in addition to those being directly tested." +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation. "]
    )
    var additionalApks: List<String>? = null

    @Option(
        names = ["--auto-google-login"],
        description = ["Automatically log into the test device using a preconfigured " +
                "Google account before beginning the test. Disabled by default."]
    )
    var autoGoogleLogin: Boolean? = null

    @Option(
        names = ["--no-auto-google-login"],
        description = ["Google account not logged in (default behavior). Use --auto-google-login to enable"]
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
        names = ["--robo-directives"],
        split = ",",
        description = [
            "A comma-separated (<type>:<key>=<value>) map of robo_directives that you can use to customize the behavior of Robo test.",
            "The type specifies the action type of the directive, which may take on values click, text or ignore.",
            "If no type is provided, text will be used by default.",
            "Each key should be the Android resource name of a target UI element and each value should be the text input for that element.",
            "Values are only permitted for text type elements, so no value should be specified for click and ignore type elements."
        ]
    )
    var roboDirectives: List<String>? = null

    @Option(
        names = ["--robo-script"],
        description = [
            "The path to a Robo Script JSON file.",
            "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.",
            "You can guide the Robo test to perform specific actions by recording a Robo Script in Android Studio and then specifying this argument.",
            "Learn more at https://firebase.google.com/docs/test-lab/robo-ux-test#scripting. "
        ]
    )
    var roboScript: String? = null

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
        names = ["--other-files"],
        split = ",",
        description = ["A list of device-path=file-path pairs that indicate the device paths to push files to the device before starting tests, and the paths of files to push." +
                "Device paths must be under absolute, whitelisted paths (\${EXTERNAL_STORAGE}, or \${ANDROID_DATA}/local/tmp)." +
                "Source file paths may be in the local filesystem or in Google Cloud Storage (gs://…). "]
    )
    var otherFiles: Map<String, String>? = null

    @Option(
        names = ["--performance-metrics"],
        description = ["Monitor and record performance metrics: CPU, memory, " +
                "network usage, and FPS (game-loop only). Disabled by default."]
    )
    var performanceMetrics: Boolean? = null

    @Option(
        names = ["--no-performance-metrics"],
        description = ["Disables performance metrics (default behavior). Use --performance-metrics to enable."]
    )
    var noPerformanceMetrics: Boolean? = null

    @Option(
        names = ["--num-uniform-shards"],
        description = ["Specifies the number of shards into which you want to evenly distribute test cases." +
                "The shards are run in parallel on separate devices. For example," +
                "if your test execution contains 20 test cases and you specify four shards, each shard executes five test cases." +
                "The number of shards should be less than the total number of test cases." +
                "The number of shards specified must be >= 1 and <= 50." +
                "This option cannot be used along max-test-shards and is not compatible with smart sharding." +
                "If you want to take benefits of smart sharding use max-test-shards."]
    )
    var numUniformShards: Int? = null

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
        names = ["--legacy-junit-result"],
        description = ["Fallback for legacy xml junit results parsing."]
    )
    var useLegacyJUnitResult: Boolean? = null
}
