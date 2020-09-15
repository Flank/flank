package ftl.args.yml

data class AppTestPair(
    val app: String?,
    val test: String,
    val environmentVariables: Map<String, String> = emptyMap()
)
