package flank.corellium.cli

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import flank.corellium.cli.util.ConfigMap
import flank.corellium.cli.util.emptyConfigMap
import flank.corellium.cli.util.loadYaml
import flank.corellium.cli.util.merge
import flank.corellium.corelliumApi
import flank.corellium.domain.RunTestAndroidCorellium
import flank.corellium.domain.invoke
import picocli.CommandLine

class RunTestCorelliumAndroidCommand :
    Runnable,
    RunTestAndroidCorellium.Context {

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Config @JsonIgnore constructor(
        @JsonIgnore
        override val data: MutableMap<String, Any?>
    ) : ConfigMap {
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
                RunTestAndroidCorellium.Apk.App(
                    path = app,
                    tests = tests.split(",").map(RunTestAndroidCorellium.Apk::Test)
                )
            }
        }

        var apks: List<RunTestAndroidCorellium.Apk.App>? by data

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

        constructor() : this(emptyConfigMap())
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

    override val args by lazy { createArgs() }

    override fun run() = invoke()
}

private fun defaultConfig() = RunTestCorelliumAndroidCommand.Config().apply {
    auth = "auth.yml"
    project = "Default Project"
    apks = emptyList()
    maxTestShards = 3
    localResultsDir = ""
}

private fun RunTestCorelliumAndroidCommand.yamlConfig(): RunTestCorelliumAndroidCommand.Config =
    yamlConfigPath?.let(::loadYaml) ?: RunTestCorelliumAndroidCommand.Config()

private fun RunTestCorelliumAndroidCommand.createArgs() = RunTestAndroidCorellium.Args(
    apks = config.apks!!,
    outputDir = config.localResultsDir!!,
    maxShardsCount = config.maxTestShards!!,
    credentials = loadYaml(config.auth!!)
)
