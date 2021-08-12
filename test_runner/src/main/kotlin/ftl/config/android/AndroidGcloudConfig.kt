package ftl.config.android

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.yml.IYmlKeys
import ftl.args.yml.ymlKeys
import ftl.config.Config
import ftl.config.FlankDefaults
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
        description = [
            "The path to the application binary file. " +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation."
        ]
    )
    @set:JsonProperty("app")
    var app: String? by data

    @set:CommandLine.Option(
        names = ["--test"],
        description = [
            "The path to the binary file containing instrumentation tests. " +
                "The given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."
        ]
    )
    @set:JsonProperty("test")
    var test: String? by data

    @set:CommandLine.Option(
        names = ["--additional-apks"],
        split = ",",
        description = [
            "A list of up to 100 additional APKs to install, in addition to those being directly tested." +
                "The path may be in the local filesystem or in Google Cloud Storage using gs:// notation. "
        ]
    )
    @set:JsonProperty("additional-apks")
    var additionalApks: List<String>? by data

    @set:CommandLine.Option(
        names = ["--auto-google-login"],
        description = [
            "Automatically log into the test device using a preconfigured " +
                "Google account before beginning the test. Disabled by default."
        ]
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
        description = [
            "Whether each test runs in its own Instrumentation instance " +
                "with the Android Test Orchestrator (default: Orchestrator is used. To disable, use --no-use-orchestrator). " +
                "Orchestrator is only compatible with AndroidJUnitRunner v1.0 or higher. See " +
                "https://developer.android.com/training/testing/junit-runner.html#using-android-test-orchestrator for more " +
                "information about Android Test Orchestrator."
        ]
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
        description = [
            "A comma-separated, key=value map of environment variables " +
                "and their desired values. --environment-variables=coverage=true,coverageFile=/sdcard/coverage.ec " +
                "The environment variables are mirrored as extra options to the am instrument -e KEY1 VALUE1 … command and " +
                "passed to your test runner (typically AndroidJUnitRunner)" +
                "If you want have downloaded coverage you need also set --directories-to-pull"
        ]
    )
    @set:JsonProperty("environment-variables")
    var environmentVariables: Map<String, String>? by data

    @set:CommandLine.Option(
        names = ["--grant-permissions"],
        description = [
            "Whether to grant runtime permissions on the device before the test begins. By default," +
                " all permissions are granted. PERMISSIONS must be one of: all, none."
        ]
    )
    @set:JsonProperty("grant-permissions")
    var grantPermissions: String? by data

    @set:CommandLine.Option(
        names = ["--scenario-labels"],
        split = ",",
        description = [
            "A list of game-loop scenario labels (default: None). " +
                "Each game-loop scenario may be labeled in the APK manifest file with one or more arbitrary strings, creating logical groupings (e.g. GPU_COMPATIBILITY_TESTS). " +
                "If --scenario-numbers and --scenario-labels are specified together, Firebase Test Lab will first execute each scenario from --scenario-numbers. " +
                "It will then expand each given scenario label into a list of scenario numbers marked with that label, and execute those scenarios."
        ]
    )
    @set:JsonProperty("scenario-labels")
    var scenarioLabels: List<String>? by data

    @set:CommandLine.Option(
        names = ["--obb-names"],
        split = ",",
        description = [
            "A list of OBB required filenames. OBB file name must conform to the format as specified by Android e.g. " +
                "## [main|patch].0300110.com.example.android.obb which will be installed into <shared-storage>/Android/obb/<package-name>/ on the device."
        ]
    )
    @set:JsonProperty("obb-names")
    var obbnames: List<String>? by data

    @set:CommandLine.Option(
        names = ["--obb-files"],
        split = ",",
        description = [
            "A list of one or two Android OBB file names which will be copied to each test device before the tests will run (default: None). " +
                "Each OBB file name must conform to the format as specified by Android " +
                "(e.g. [main|patch].0300110.com.example.android.obb) and will be installed into <shared-storage>/Android/obb/<package-name>/ on the test device."
        ]
    )
    @set:JsonProperty("obb-files")
    var obbfiles: List<String>? by data

    @set:CommandLine.Option(
        names = ["--performance-metrics"],
        description = [
            "Monitor and record performance metrics: CPU, memory, " +
                "network usage, and FPS (game-loop only). Disabled by default."
        ]
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
        description = [
            "Specifies the number of shards into which you want to evenly distribute test cases." +
                "The shards are run in parallel on separate devices. For example," +
                "if your test execution contains 20 test cases and you specify four shards, each shard executes five test cases." +
                "The number of shards should be less than the total number of test cases." +
                "The number of shards specified must be >= 1 and <= 50." +
                "This option cannot be used along max-test-shards and is not compatible with smart sharding." +
                "If you want to take benefits of smart sharding use max-test-shards."
        ]
    )
    @set:JsonProperty("num-uniform-shards")
    var numUniformShards: Int? by data

    @set:CommandLine.Option(
        names = ["--test-runner-class"],
        description = [
            "The fully-qualified Java class name of the instrumentation test runner (default: the last name extracted " +
                "from the APK manifest)."
        ]
    )
    @set:JsonProperty("test-runner-class")
    var testRunnerClass: String? by data

    @set:CommandLine.Option(
        names = ["--test-targets"],
        split = ",",
        description = [
            "A list of one or more test target filters to apply " +
                "(default: run all test targets). Each target filter must be fully qualified with the package name, class name, " +
                "or test annotation desired. Any test filter supported by am instrument -e … is supported. " +
                "See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more " +
                "information. You can also pass values as environment variables: \$TEST_TARGETS_IN_ENV. This can be also list of " +
                "strings delimited by coma."
        ]
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

    @set:CommandLine.Option(
        names = ["--test-targets-for-shard"],
        description = [
            "Specifies a group of packages, classes, and/or test cases to run in each shard (a group of test cases)." +
                " The shards are run in parallel on separate devices. " +
                "You can repeat this flag up to 50 times to specify multiple shards when one or more physical devices are selected, or up to 500 times when no physical devices are selected.\n" +
                "Note: If you include the flags --environment-variable or --test-targets when running --test-targets-for-shard, the flags are applied to all the shards you create"
        ]
    )
    @set:JsonProperty("test-targets-for-shard")
    var testTargetsForShard: List<String>? by data

    @set:CommandLine.Option(
        names = ["--parameterized-tests"],
        description = [
            "Specifies how to handle tests which contain the parameterization annotation. Possible values: `default`, `ignore-all`, `shard-into-single`, `shard-into-multiple`.\n" +
                "leaving it blank will result in `default` sharding.\n" +
                "Note: Making use of shard-into-single` or `shard-into-multiple will result in additional shards being created even if a max number of shards has been specified.\n" +
                "Note: If shard-into-single is used, a single additional shard is created that will run the Parameterized tests separately.\n" +
                "Note: If shard-into-multiple is used, each parameterized test will be matched by its corresponding name and sharded into a separate shard. This may dramatically increase the amount of expected shards depending upon how many parameterized tests are discovered."
        ]
    )
    @set:JsonProperty("parameterized-tests")
    var parameterizedTests: String? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys {

        override val group = IYmlKeys.Group.GCLOUD

        override val keys by lazy {
            AndroidGcloudConfig::class.ymlKeys
        }

        fun default() = AndroidGcloudConfig().apply {
            app = null
            test = null
            additionalApks = emptyList()
            autoGoogleLogin = FlankDefaults.DISABLE_AUTO_LOGIN
            useOrchestrator = true
            environmentVariables = emptyMap()
            grantPermissions = FlankDefaults.GRANT_PERMISSIONS_ALL
            scenarioLabels = emptyList()
            obbfiles = emptyList()
            obbnames = emptyList()
            performanceMetrics = FlankDefaults.DISABLE_PERFORMANCE_METRICS
            numUniformShards = null
            testRunnerClass = null
            testTargets = emptyList()
            roboDirectives = emptyMap()
            roboScript = null
            testTargetsForShard = emptyList()
            parameterizedTests = FlankDefaults.DEFAULT_PARAMETERIZED_TESTS
        }
    }
}
