package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory


@JsonIgnoreProperties(ignoreUnknown = true)
data class AppTestPair(
    val app: String?,
    val test: String,
    @JsonProperty("environment-variables")
    val environmentVariables: Map<String, String> = emptyMap(),
    @JsonProperty("max-test-shards")
    val maxTestShards: Int = -1,
    @JsonProperty("client-details")
    var clientDetails: Map<String, String> = emptyMap()
)
