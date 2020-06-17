package ftl.run.model

import com.google.gson.annotations.SerializedName

data class AndroidTestShards(
    val app: String,
    val test: String,
    val shards: Map<String, List<String>> = emptyMap(),
    @SerializedName("junit-ignored")
    val junitIgnored: List<String> = emptyList()
)
