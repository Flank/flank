package flank.corellium.cli

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import flank.apk.Apk
import flank.corellium.cli.RunTestCorelliumAndroidCommand.Config
import flank.corellium.cli.util.ConfigMap
import flank.corellium.cli.util.emptyConfigMap
import flank.corellium.cli.util.loadYaml
import flank.corellium.cli.util.merge
import flank.corellium.corelliumApi
import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.Args
import flank.corellium.domain.invoke
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

    override val apk = Apk.Api()

    override val args by lazy { createArgs() }

    override fun run() = invoke()
}

private fun defaultConfig() = Config().apply {
    project = "Default Project"
    auth = "corellium_auth.yml"
    apks = emptyList()
    maxTestShards = 1
    localResultsDir = null
    obfuscate = false
}

private fun RunTestCorelliumAndroidCommand.yamlConfig(): Config =
    yamlConfigPath?.let(::loadYaml) ?: Config()

private fun RunTestCorelliumAndroidCommand.createArgs() = Args(
    credentials = loadYaml(config.auth!!),
    apks = config.apks!!,
    maxShardsCount = config.maxTestShards!!,
    outputDir = config.localResultsDir ?: Args.DefaultOutputDir.new,
    obfuscateDumpShards = config.obfuscate!!,
)
