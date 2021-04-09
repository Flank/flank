package utils

import com.fasterxml.jackson.annotation.JsonProperty

typealias AndroidMatrixTestShards = Map<String, AndroidTestShards>

data class AndroidTestShards(
    val app: String,
    val test: String,
    val shards: Map<String, List<String>> = emptyMap(),
    @JsonProperty("junit-ignored")
    val junitIgnored: List<String> = emptyList()
)

typealias IosMatrixTestShards = List<Map<String, List<String>>>
