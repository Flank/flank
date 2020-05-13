package ftl.args.yml.errors

internal class ConfigurationErrorParser {

    //region regex patterns
    private val propertyNameRegex = "(?<=property\\s)[a-z]*".toRegex()
    private val referenceChainRegex = "(?<=chain:\\s).*(?=[)])".toRegex()
    private val referenceChainCleanUpRegex = "(?<=[\\[])\"?[\\w]*\"?(?=])".toRegex()
    private val lineAndColumnRegex = "((?<=line:\\s)\\d*), column:\\s(\\d*)".toRegex()
    //endregion

    operator fun invoke(errorMessage: String): ConfigurationErrorModel {
        val (line, column) = parseErrorPositionLine(errorMessage)
        return ConfigurationErrorModel(
            parsePropertyName(errorMessage),
            line.toInt(),
            column.toInt(),
            parseReferenceChain(errorMessage)
        )
    }

    private fun parsePropertyName(errorMessage: String) = propertyNameRegex.find(errorMessage)?.value ?: ""
    private fun parseErrorPositionLine(errorMessage: String) = lineAndColumnRegex.find(errorMessage)!!.destructured

    private fun parseReferenceChain(errorMessage: String) =
        cleanUpReferenceChain(referenceChainRegex.find(errorMessage)!!.value)

    private fun cleanUpReferenceChain(referenceChain: String): List<String> =
        referenceChainCleanUpRegex.findAll(referenceChain).map { it.value.replace("\"", "") }.toList()
}
