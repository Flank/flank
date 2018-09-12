package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class IosFlankYmlParams(
    @field:JsonProperty("test-targets")
    val testTargets: List<String> = emptyList()
) {
    companion object : IYmlKeys {
        override val keys = listOf("test-targets")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class IosFlankYml(
    // when an empty 'flank:' is present in a yaml then parsedFlank will be parsed as null.
    @field:JsonProperty("flank")
    private val parsedFlank: IosFlankYmlParams? = IosFlankYmlParams()
) {
    val flank = parsedFlank ?: IosFlankYmlParams()
    companion object : IYmlMap {
        override val map = mapOf("flank" to IosFlankYmlParams.keys)
    }
}
