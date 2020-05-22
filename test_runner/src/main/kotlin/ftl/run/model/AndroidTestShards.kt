package ftl.run.model

data class AndroidTestShards(
    val app: String,
    val test: String,
    val shards: Map<String, List<String>> = emptyMap()
)
