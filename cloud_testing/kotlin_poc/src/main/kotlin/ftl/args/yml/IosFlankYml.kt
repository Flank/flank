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
        val flank: IosFlankYmlParams = IosFlankYmlParams()
) {
    companion object : IYmlMap  {
        override val map = mapOf("flank" to IosFlankYmlParams.keys)
    }
}
