package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/** Flank specific parameters for both iOS and Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class IosFlankYmlParams(
        val testTargets: List<String> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
class IosFlankYml(
        val flank: IosFlankYmlParams = IosFlankYmlParams(),
        @JsonIgnore val gcloud: Any? = null
)
