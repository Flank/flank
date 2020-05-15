package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.ArgsHelper
import ftl.config.FtlConstants

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class FlankYmlParams(
    @field:JsonProperty("max-test-shards")
    val maxTestShards: Int = 1,

    @field:JsonProperty("shard-time")
    val shardTime: Int = -1,

    @field:JsonProperty("num-test-runs")
    val repeatTests: Int = 1,

    @field:JsonProperty("smart-flank-gcs-path")
    val smartFlankGcsPath: String = "",

    @field:JsonProperty("smart-flank-disable-upload")
    val smartFlankDisableUpload: Boolean = false,

    @field:JsonProperty("disable-sharding")
    val disableSharding: Boolean = false,

    @field:JsonProperty("test-targets-always-run")
    val testTargetsAlwaysRun: List<String> = emptyList(),

    @field:JsonProperty("files-to-download")
    val filesToDownload: List<String> = emptyList(),

    val project: String = ArgsHelper.getDefaultProjectId() ?: "",

    @field:JsonProperty("local-result-dir")
    val localResultsDir: String = defaultLocalResultsDir,

    @field:JsonProperty("run-timeout")
    val runTimeout: String = FtlConstants.runTimeout,

    @field:JsonProperty("legacy-junit-result")
    val useLegacyJUnitResult: Boolean = false,

    @field:JsonProperty("ignore-failed-tests")
    val ignoreFailedTests: Boolean = false,

    @field:JsonProperty("keep-file-path")
    val keepFilePath: Boolean = false,

    @field:JsonProperty("output-style")
    val outputStyle: String? = null
) {
    companion object : IYmlKeys {
        override val keys = listOf(
            "max-test-shards",
            "shard-time",
            "num-test-runs",
            "smart-flank-gcs-path",
            "smart-flank-disable-upload",
            "disable-sharding",
            "test-targets-always-run",
            "files-to-download",
            "project",
            "local-result-dir",
            "run-timeout",
            "legacy-junit-result",
            "ignore-failed-tests",
            "keep-file-path",
            "output-style"
        )

        const val defaultLocalResultsDir = "results"
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class FlankYml(
    // when an empty 'flank:' is present in a yaml then parsedFlank will be parsed as null.
    @field:JsonProperty("flank")
    private val parsedFlank: FlankYmlParams? = FlankYmlParams()
) {
    val flank = parsedFlank ?: FlankYmlParams()

    companion object : IYmlMap {
        override val map = mapOf("flank" to FlankYmlParams.keys)
    }
}
