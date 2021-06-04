package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.Device

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppTestPair(
    val app: String?,
    val test: String,
    @JsonProperty("environment-variables")
    val environmentVariables: Map<String, String> = emptyMap(),
    @JsonProperty("max-test-shards")
    val maxTestShards: Int? = null,
    @JsonProperty("client-details")
    val clientDetails: Map<String, String>? = null,
    @JsonProperty("test-targets")
    val testTargets: List<String>? = null,
    @JsonProperty("device")
    val devices: List<Device>? = null
)
