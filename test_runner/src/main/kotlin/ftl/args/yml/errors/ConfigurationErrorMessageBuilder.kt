package ftl.args.yml.errors

import com.fasterxml.jackson.databind.JsonNode
import java.lang.Exception

class ConfigurationErrorMessageBuilder {

    private val parseMessage = ConfigurationErrorParser()
    private val resolveErrorNode = ErrorNodeResolver()

    //region error message elements
    private val messageHeader = "Error on parse config: "
    private val missingElementMessage = "Missing element or value for: '%s'"
    private val atMessage = "At line: %s, column: %s"
    private val errorNodeMessage = "Error node: %s"
    //endregion

    private val exceptionTemplate = "Parse message error: %s"

    operator fun invoke(errorMessage: String, yamlTreeNode: JsonNode? = null) =
        try {
            val errorModel = parseMessage(errorMessage)
            val errorMessageBuilder = StringBuilder(messageHeader)
            errorMessageBuilder.appendln(createReferenceChain(errorModel.referenceChain))
            if (errorModel.propertyName != "") {
                errorMessageBuilder.appendln(missingElementMessage.format(errorModel.propertyName))
            }
            errorMessageBuilder.appendln(atMessage.format(errorModel.line, errorModel.column))
            yamlTreeNode?.let {
                errorMessageBuilder.appendln(errorNodeMessage.format(resolveErrorNode(yamlTreeNode, errorModel)))
            }
            errorMessageBuilder.toString().trim()
        } catch (error: Exception) {
            exceptionTemplate.format(errorMessage)
        }

    private fun createReferenceChain(referenceChain: List<String>): String {
        val chainBuilder = StringBuilder()
        referenceChain.forEachIndexed { index, chainPart ->
            chainBuilder.append(appendChainElement(chainPart, index > 0))
        }
        return chainBuilder.toString()
    }

    private fun appendChainElement(chainPart: String, withSeparator: Boolean): String = when {
        chainPart.toIntOrNull() != null -> "[$chainPart]"
        withSeparator -> "->$chainPart"
        else -> chainPart
    }
}
