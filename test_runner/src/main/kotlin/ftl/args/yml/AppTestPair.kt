package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class AppTestPair(
    val app: String?,
    val test: String,
    @JsonProperty("environment-variables")
    val environmentVariables: Map<String, String> = emptyMap()
)
