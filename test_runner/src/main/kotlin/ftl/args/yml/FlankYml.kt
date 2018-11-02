package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.util.Utils.fatalError

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class FlankYmlParams(
    val testShards: Int = 1,
    val repeatTests: Int = 1,
    val shuffleTests : Boolean = false,

    @field:JsonProperty("test-targets-always-run")
    val testTargetsAlwaysRun: List<String> = emptyList()
) {
    companion object : IYmlKeys {
        override val keys = listOf("testShards", "repeatTests", "test-targets-always-run", "shuffleTests")
    }

    init {
        if (testShards <= 0 && testShards != -1) fatalError("testShards must be >= 1 or -1")
        if (repeatTests < 1) fatalError("repeatTests must be >= 1")
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
