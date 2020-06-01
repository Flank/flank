package ftl.config.android

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.Config
import ftl.args.yml.IYmlKeys
import ftl.config.Device
import ftl.config.FlankDefaults
import ftl.config.FtlConstants
import picocli.CommandLine

/**
 * Android specific gcloud parameters
 *
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 */
@CommandLine.Command
@JsonIgnoreProperties(ignoreUnknown = true)
data class AndroidGcloudConfig @JsonIgnore constructor(
    @JsonIgnore
    override val data: MutableMap<String, Any?>
) : Config {

    @set:CommandLine.Option(
        names = ["--app"],
        description = ["The path to the application binary file. " +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation."]
    )
    var app: String? by data

    @set:CommandLine.Option(
        names = ["--test"],
        description = ["The path to the binary file containing instrumentation tests. " +
                "The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."]
    )
    var test: String? by data

    @set:CommandLine.Option(
        names = ["--additional-apks"],
        split = ",",
        description = ["A list of up to 100 additional APKs to install, in addition to those being directly tested." +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation. "]
    )
    @set:JsonProperty("additional-apks")
    var additionalApks: List<String>? by data

    @set:CommandLine.Option(
        names = ["--auto-google-login"],
        description = ["Automatically log into the test device using a preconfigured " +
                "Google account before beginning the test. Disabled by default."]
    )
    @set:JsonProperty("auto-google-login")
    var autoGoogleLogin: Boolean? by data

    @CommandLine.Option(
        names = ["--no-auto-google-login"],
        description = ["Google account not logged in (default behavior). Use --auto-google-login to enable"]
    )
    @JsonIgnore
    fun noAutoGoogleLogin(value: Boolean?) {
        autoGoogleLogin = value?.not() ?: false
    }

    @set:CommandLine.Option(
        names = ["--use-orchestrator"],
        description = ["Whether each test runs in its own Instrumentation instance " +
                "with the Android Test Orchestrator (default: Orchestrator is used. To disable, use --no-use-orchestrator). " +
                "Orchestrator is only compatible with AndroidJUnitRunner v1.0 or higher. See " +
                "https://developer.android.com/training/testing/junit-runner.html#using-android-test-orchestrator for more " +
                "information about Android Test Orchestrator."]
    )
    @set:JsonProperty("use-orchestrator")
    var useOrchestrator: Boolean? by data

    @CommandLine.Option(
        names = ["--no-use-orchestrator"],
        description = ["Orchestrator is not used. See --use-orchestrator."]
    )
    fun noUseOrchestrator(value: Boolean?) {
        useOrchestrator = value?.not() ?: false
    }

    @set:CommandLine.Option(
        names = ["--environment-variables"],
        split = ",",
        description = ["A comma-separated, key=value map of environment variables " +
                "and their desired values. --environment-variables=coverage=true,coverageFile=/sdcard/coverage.ec " +
                "The environment variables are mirrored as extra options to the am instrument -e KEY1 VALUE1 … command and " +
                "passed to your test runner (typically AndroidJUnitRunner)"]
    )
    @set:JsonProperty("environment-variables")
    var environmentVariables: Map<String, String>? by data

    @set:CommandLine.Option(
        names = ["--directories-to-pull"],
        split = ",",
        description = ["A list of paths that will be copied from the device's " +
                "storage to the designated results bucket after the test is complete. These must be absolute paths under " +
                "/sdcard or /data/local/tmp (for example, --directories-to-pull /sdcard/tempDir1,/data/local/tmp/tempDir2). " +
                "Path names are restricted to the characters a-zA-Z0-9_-./+. The paths /sdcard and /data will be made available " +
                "and treated as implicit path substitutions. E.g. if /sdcard on a particular device does not map to external " +
                "storage, the system will replace it with the external storage path prefix for that device."]
    )
    @set:JsonProperty("directories-to-pull")
    var directoriesToPull: List<String>? by data

    @set:CommandLine.Option(
        names = ["--other-files"],
        split = ",",
        description = ["A list of device-path=file-path pairs that indicate the device paths to push files to the device before starting tests, and the paths of files to push." +
                "Device paths must be under absolute, whitelisted paths (\${EXTERNAL_STORAGE}, or \${ANDROID_DATA}/local/tmp)." +
                "Source file paths may be in the local filesystem or in Google Cloud Storage (gs://…). "]
    )
    @set:JsonProperty("other-files")
    var otherFiles: Map<String, String>? by data

    @set:CommandLine.Option(
        names = ["--performance-metrics"],
        description = ["Monitor and record performance metrics: CPU, memory, " +
                "network usage, and FPS (game-loop only). Disabled by default."]
    )
    @set:JsonProperty("performance-metrics")
    var performanceMetrics: Boolean? by data

    @CommandLine.Option(
        names = ["--no-performance-metrics"],
        description = ["Disables performance metrics (default behavior). Use --performance-metrics to enable."]
    )
    @JsonIgnore
    fun noPerformanceMetrics(value: Boolean?) {
        performanceMetrics = value?.not() ?: false
    }

    @set:CommandLine.Option(
        names = ["--num-uniform-shards"],
        description = ["Specifies the number of shards into which you want to evenly distribute test cases." +
                "The shards are run in parallel on separate devices. For example," +
                "if your test execution contains 20 test cases and you specify four shards, each shard executes five test cases." +
                "The number of shards should be less than the total number of test cases." +
                "The number of shards specified must be >= 1 and <= 50." +
                "This option cannot be used along max-test-shards and is not compatible with smart sharding." +
                "If you want to take benefits of smart sharding use max-test-shards."]
    )
    @set:JsonProperty("num-uniform-shards")
    var numUniformShards: Int? by data

    @set:CommandLine.Option(
        names = ["--test-runner-class"],
        description = ["The fully-qualified Java class name of the instrumentation test runner (default: the last name extracted " +
                "from the APK manifest)."]
    )
    @set:JsonProperty("test-runner-class")
    var testRunnerClass: String? by data

    @set:CommandLine.Option(
        names = ["--test-targets"],
        split = ",",
        description = ["A list of one or more test target filters to apply " +
                "(default: run all test targets). Each target filter must be fully qualified with the package name, class name, " +
                "or test annotation desired. Any test filter supported by am instrument -e … is supported. " +
                "See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more " +
                "information."]
    )
    @set:JsonProperty("test-targets")
    var testTargets: List<String?>? by data

    @set:CommandLine.Option(
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
    @set:JsonProperty("robo-directives")
    var roboDirectives: Map<String, String>? by data

    @set:CommandLine.Option(
        names = ["--robo-script"],
        description = [
            "The path to a Robo Script JSON file.",
            "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation.",
            "You can guide the Robo test to perform specific actions by recording a Robo Script in Android Studio and then specifying this argument.",
            "Learn more at https://firebase.google.com/docs/test-lab/robo-ux-test#scripting. "
        ]
    )
    @set:JsonProperty("robo-script")
    var roboScript: String? by data

    @CommandLine.Option(
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
            model = map.getOrDefault("model", FtlConstants.defaultAndroidModel),
            version = map.getOrDefault("version", FtlConstants.defaultAndroidVersion),
            locale = map.getOrDefault("locale", FtlConstants.defaultLocale),
            orientation = map.getOrDefault("orientation", FtlConstants.defaultOrientation)
        )

        if (device == null) device = mutableListOf()
        device?.add(androidDevice)
    }

    var device: MutableList<Device>? by data

    constructor() : this(mutableMapOf())

    companion object : IYmlKeys {
        override val keys = listOf(
            "app",
            "test",
            "additional-apks",
            "auto-google-login",
            "use-orchestrator",
            "environment-variables",
            "directories-to-pull",
            "other-files",
            "performance-metrics",
            "num-uniform-shards",
            "test-runner-class",
            "test-targets",
            "robo-directives",
            "robo-script",
            "device"
        )

        fun default() = AndroidGcloudConfig().apply {
            app = null
            test = null
            additionalApks = emptyList()
            autoGoogleLogin = FlankDefaults.DISABLE_AUTO_LOGIN
            useOrchestrator = true
            environmentVariables = emptyMap()
            directoriesToPull = emptyList()
            otherFiles = emptyMap()
            performanceMetrics = FlankDefaults.DISABLE_PERFORMANCE_METRICS
            numUniformShards = null
            testRunnerClass = null
            testTargets = emptyList()
            roboDirectives = emptyMap()
            roboScript = null
            device = mutableListOf(Device(FtlConstants.defaultAndroidModel, FtlConstants.defaultAndroidVersion))
        }
    }
}
