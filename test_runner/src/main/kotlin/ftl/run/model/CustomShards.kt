package ftl.run.model

data class CustomShards(
    val app: String?,
    val test: String?,
    val shards: List<List<String>>,
    val zip: String?
)
