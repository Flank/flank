package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.util.Utils.fatalError

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class FlankYmlParams(
    val testShards: Int = 1,
    val testRuns: Int = 1,

    @field:JsonProperty("test-targets-always-run")
    val testTargetsAlwaysRun: List<String> = emptyList()
) {
    companion object : IYmlKeys {
        override val keys = listOf("testShards", "testRuns", "test-targets-always-run")
    }

    init {
        if (testShards <= 0 && testShards != -1) fatalError("testShards must be >= 1 or -1")
        if (testRuns < 1) fatalError("testRuns must be >= 1")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class FlankYml(
    val flank: FlankYmlParams = FlankYmlParams()
) {
    companion object : IYmlMap {
        override val map = mapOf("flank" to FlankYmlParams.keys)
    }
}
