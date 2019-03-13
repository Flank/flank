package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.FtlConstants.GCS_PREFIX
import ftl.util.Utils.fatalError

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class FlankYmlParams(
    @field:JsonProperty("max-test-shards")
    val maxTestShards: Int = 1,

    @field:JsonProperty("shard-time")
    val shardTime: Int = -1,

    @field:JsonProperty("repeat-tests")
    val repeatTests: Int = 1,

    @field:JsonProperty("smart-flank-gcs-path")
    val smartFlankGcsPath: String = "",

    @field:JsonProperty("disable-sharding")
    val disableSharding: Boolean = false,

    @field:JsonProperty("test-targets-always-run")
    val testTargetsAlwaysRun: List<String> = emptyList(),

    @field:JsonProperty("files-to-download")
    val filesToDownload: List<String> = emptyList()
) {
    companion object : IYmlKeys {
        override val keys = listOf(
            "max-test-shards", "shard-time", "repeat-tests", "smart-flank-gcs-path", "disable-sharding",
            "test-targets-always-run", "files-to-download"
        )
    }

    init {
        if (maxTestShards <= 0 && maxTestShards != -1) fatalError("max-test-shards must be >= 1 or -1")
        if (shardTime <= 0 && shardTime != -1) fatalError("shard-time must be >= 1 or -1")
        if (repeatTests < 1) fatalError("repeat-tests must be >= 1")

        if (smartFlankGcsPath.isNotEmpty()) {
            if (!smartFlankGcsPath.startsWith(GCS_PREFIX)) {
                fatalError("smart-flank-gcs-path must start with gs://")
            }
            if (smartFlankGcsPath.count { it == '/' } <= 2 || !smartFlankGcsPath.endsWith(".xml")) {
                fatalError("smart-flank-gcs-path must be in the format gs://bucket/foo.xml")
            }
        }
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
