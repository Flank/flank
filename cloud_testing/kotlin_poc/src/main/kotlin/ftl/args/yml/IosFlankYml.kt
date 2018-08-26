package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class IosFlankYmlParams(
        @field:JsonProperty("test-targets")
        val testTargets: List<String> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
class IosFlankYml(
        val flank: IosFlankYmlParams = IosFlankYmlParams()
)
