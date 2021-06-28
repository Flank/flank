package flank.corellium.cli

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import flank.apk.Apk
import flank.corellium.api.AndroidApps
import flank.corellium.api.AndroidInstance
import flank.corellium.cli.RunTestCorelliumAndroidCommand.Config
import flank.corellium.cli.util.ConfigMap
import flank.corellium.cli.util.emptyConfigMap
import flank.corellium.cli.util.loadYaml
import flank.corellium.cli.util.merge
import flank.corellium.corelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import flank.corellium.domain.RunTestCorelliumAndroid.Authorize
import flank.corellium.domain.RunTestCorelliumAndroid.CleanUp
import flank.corellium.domain.RunTestCorelliumAndroid.CompleteTests
import flank.corellium.domain.RunTestCorelliumAndroid.DumpShards
import flank.corellium.domain.RunTestCorelliumAndroid.ExecuteTests
import flank.corellium.domain.RunTestCorelliumAndroid.GenerateReport
import flank.corellium.domain.RunTestCorelliumAndroid.InstallApks
import flank.corellium.domain.RunTestCorelliumAndroid.InvokeDevices
import flank.corellium.domain.RunTestCorelliumAndroid.LoadPreviousDurations
import flank.corellium.domain.RunTestCorelliumAndroid.OutputDir
import flank.corellium.domain.RunTestCorelliumAndroid.ParseApkInfo
import flank.corellium.domain.RunTestCorelliumAndroid.ParseTestCases
import flank.corellium.domain.RunTestCorelliumAndroid.PrepareShards
import flank.corellium.domain.invoke
import flank.instrument.log.Instrument
import flank.junit.JUnit
import flank.log.Event.Start
import flank.log.buildFormatter
import flank.log.output
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
class RunTestCorelliumAndroidCommand :
    Runnable,
    RunTestCorelliumAndroid.Context {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Config @JsonIgnore constructor(
        @JsonIgnore
        override val data: MutableMap<String, Any?>
    ) : ConfigMap {
        constructor() : this(emptyConfigMap())

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
            names = ["-h", "--help"],
            usageHelp = true,
            description = ["Prints this help message"]
        )
        var usageHelpRequested: Boolean = false

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

    @CommandLine.Mixin
    val cliConfig = Config()

    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var yamlConfigPath: String? = null

    internal val config by lazy { merge(defaultConfig(), yamlConfig(), cliConfig) }

    override val api by lazy { corelliumApi(config.project!!) }

    override val apk by lazy { Apk.Api() }

    override val junit by lazy { JUnit.Api() }

    override val args by lazy { createArgs() }

    override val out by lazy { format.output }

    override fun run() = invoke()
}

private fun defaultConfig() = Config().apply {
    project = "Default Project"
    auth = "corellium_auth.yml"
    apks = emptyList()
    maxTestShards = 1
    localResultsDir = null
    obfuscate = false
    gpuAcceleration = true
    scanPreviousDurations = 10
}

private fun RunTestCorelliumAndroidCommand.yamlConfig(): Config =
    yamlConfigPath?.let(::loadYaml) ?: Config()

private fun RunTestCorelliumAndroidCommand.createArgs() = Args(
    credentials = loadYaml(config.auth!!),
    apks = config.apks!!,
    maxShardsCount = config.maxTestShards!!,
    outputDir = config.localResultsDir ?: Args.DefaultOutputDir.new,
    obfuscateDumpShards = config.obfuscate!!,
    gpuAcceleration = config.gpuAcceleration!!,
    scanPreviousDurations = config.scanPreviousDurations!!,
)

internal val format = buildFormatter<String> {

    Start(Authorize) { "* Authorizing" }
    Start(CleanUp) { "* Cleaning instances" }
    Start(OutputDir) { "* Preparing output directory" }
    Start(DumpShards) { "* Dumping shards" }
    Start(ExecuteTests) { "* Executing tests" }
    Start(CompleteTests) { "* Finish" }
    Start(GenerateReport) { "* Generating report" }
    Start(InstallApks) { "* Installing apks" }
    Start(InvokeDevices) { "* Invoking devices" }
    Start(LoadPreviousDurations) { "* Obtaining previous test cases durations" }
    Start(ParseApkInfo) { "* Parsing apk info" }
    Start(ParseTestCases) { "* Parsing test cases" }
    Start(PrepareShards) { "* Calculating shards" }

    LoadPreviousDurations.Searching { "Searching in $this JUnitReport.xml files..." }
    LoadPreviousDurations.Summary::class { "For $required test cases, found $matching matching and $unknown unknown" }
    InstallApks.Status {
        when (this) {
            is AndroidApps.Event.Connecting.Agent -> "Connecting agent for $instanceId"
            is AndroidApps.Event.Connecting.Console -> "Connecting console for $instanceId"
            is AndroidApps.Event.Apk.Uploading -> "Uploading apk $path"
            is AndroidApps.Event.Apk.Installing -> "Installing apk $path"
        }
    }
    InvokeDevices.Status {
        when (this) {
            is AndroidInstance.Event.GettingAlreadyCreated -> "Getting instances already created by flank."
            is AndroidInstance.Event.Obtained -> "Obtained $size already created devices"
            is AndroidInstance.Event.Starting -> "Starting not running $size instances."
            is AndroidInstance.Event.Started -> "$id - $name"
            is AndroidInstance.Event.Creating -> "Creating additional $size instances. Connecting to the agents may take longer."
            is AndroidInstance.Event.Waiting -> "Wait until all instances are ready..."
            is AndroidInstance.Event.Ready -> "ready: $id"
            is AndroidInstance.Event.AllReady -> "All instances invoked and ready to use."
        }
    }
    ExecuteTests.Status::class {
        when (val status = status) {
            is Instrument.Status -> "$id: " + status.details.run { "$className#$testName" } + " - " + status.code
            else -> null
        }
    }
    ExecuteTests.Error::class {
        """
            Error while parsing results from instance $id.
            For details check $logFile lines $lines.
            
        """.trimIndent() + cause.stackTraceToString()
    }
    RunTestCorelliumAndroid.Created { "Created $path" }
    RunTestCorelliumAndroid.AlreadyExist { "Already exist $path" }

    match { it as? String } to { this }
}
