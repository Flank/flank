package ftl.args.yml.errors

internal data class ConfigurationErrorModel(val propertyName: String, val line: Int, val column: Int, val referenceChain: List<String>)
