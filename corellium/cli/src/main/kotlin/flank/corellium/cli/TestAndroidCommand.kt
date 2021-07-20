package flank.corellium.cli

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import flank.config.ConfigMap
import flank.config.emptyConfigMap
import flank.corellium.cli.test.android.format
import flank.corellium.cli.test.android.task.apkApi
import flank.corellium.cli.test.android.task.args
import flank.corellium.cli.test.android.task.config
import flank.corellium.cli.test.android.task.corelliumApi
import flank.corellium.cli.test.android.task.jUnitApi
import flank.corellium.domain.TestAndroid.Args
import flank.corellium.domain.TestAndroid.CompleteTests
import flank.corellium.domain.TestAndroid.execute
import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.invoke
import flank.exection.parallel.verify
import flank.log.output
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import picocli.CommandLine

@CommandLine.Command(
    name = "run",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Run tests on Corellium Android instances"],
    usageHelpAutoWidth = true
)
class TestAndroidCommand :
    Runnable {

    class Context : Parallel.Context() {
        val command by !TestAndroidCommand
        val config by -Config
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Config @JsonIgnore constructor(
        @JsonIgnore
        override val data: MutableMap<String, Any?>
    ) : ConfigMap {
        constructor() : this(emptyConfigMap())

        companion object : Parallel.Type<Config>

        @set:CommandLine.Option(
            names = ["-a", "--auth"],
            description = ["YAML authorization file path"]
        )
        var auth: String? by data

        @set:CommandLine.Option(
            names = ["-p", "--project"],
            description = ["YAML credentials file path"]
        )
        var project: String? by data

        @CommandLine.Option(
            names = ["--apks"],
            split = ";",
            description = [
                "A list of app & test apks to include in the run. " +
                    "Useful for running multiple module tests within a single Flank run."
            ]
        )
        fun addApks(map: Map<String, String>) {
            apks = (apks ?: emptyList()) + map.map { (app, tests) ->
                Args.Apk.App(
                    path = app,
                    tests = tests.split(",").map(Args.Apk::Test)
                )
            }
        }

        var apks: List<Args.Apk.App>? by data

        @set:CommandLine.Option(
            names = ["--test-targets"],
            split = ",",
            description = [
                "A list of one or more test target filters to apply " +
                    "(default: run all test targets). Each target filter must be fully qualified with the package name, class name, " +
                    "or test annotation desired. Any test filter supported by am instrument -e … is supported. " +
                    "See https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner for more " +
                    "information."
            ]
        )
        @set:JsonProperty("test-targets")
        var testTargets: List<String>? by data

        @set:CommandLine.Option(
            names = ["--max-test-shards"],
            description = ["The amount of matrices to split the tests across."]
        )
        @set:JsonProperty("max-test-shards")
        var maxTestShards: Int? by data

        @set:CommandLine.Option(
            names = ["--local-result-dir"],
            description = ["Saves test result to this local folder. Deleted before each run."]
        )
        @set:JsonProperty("local-result-dir")
        var localResultsDir: String? by data

        @set:CommandLine.Option(
            names = ["--obfuscate"],
            description = [
                "Replacing internal test names with unique identifiers when using --dump-shards option " +
                    "to avoid exposing internal details"
            ]
        )
        var obfuscate: Boolean? by data

        @set:CommandLine.Option(
            names = ["--gpu-acceleration"],
            description = [
                "Enable cloud GPU acceleration (Extra costs incurred)." +
                    "Currently this option only works for newly devices created." +
                    "To create new device pool with gpu-acceleration, remove old devices manually and let Flank recreate the pool."
            ]
        )
        @set:JsonProperty("gpu-acceleration")
        var gpuAcceleration: Boolean? by data

        @set:CommandLine.Option(
            names = ["--scan-previous-durations"],
            description = [
                "Scan the specified amount of JUnitReport.xml files to obtain test cases durations necessary for optimized sharding." +
                    "The `local-result-dir` is used for searching JUnit reports."
            ]
        )
        @set:JsonProperty("scan-previous-durations")
        var scanPreviousDurations: Int? by data
    }

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    @CommandLine.Mixin
    val cliConfig = Config()

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var yamlConfigPath: String? = null

    override fun run() {
        val seed: ParallelState = mapOf(
            TestAndroidCommand to this,
            Parallel.Logger to format.output,
        )
        runBlocking {
            resolve(seed).last().verify().let { args ->
                execute(CompleteTests)(args).last().verify()
            }
        }
    }

    internal companion object : Parallel.Type<TestAndroidCommand> {

        val context = Parallel.Function(TestAndroidCommand::Context)

        // Needs to be evaluated lazy due to strange NullPointerException when RunTestCorelliumAndroidCommandTest is run after ArgsKtTest.
        val resolve by lazy {
            setOf(
                context.validate,
                config,
                args,
                corelliumApi,
                apkApi,
                jUnitApi,
            )
        }
    }
}
