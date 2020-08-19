@file:Suppress("DEPRECATION")

package ftl.args.yml.errors

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException
import java.lang.Exception

object ConfigurationErrorMessageBuilder {

    private val parseMessage = ConfigurationErrorParser
    private val resolveErrorNode = ErrorNodeResolver

    //region error message elements
    private const val messageHeader = "Error on parse config: "
    private const val missingElementMessage = "Missing element or value for: '%s'"
    private const val atMessage = "At line: %s, column: %s"
    private const val errorNodeMessage = "Error node: %s"
    //endregion

    private const val exceptionTemplate = "Parse message error: %s"

    operator fun invoke(errorMessage: String, yamlTreeNode: JsonNode? = null) =
        try {
            val errorModel = parseMessage(errorMessage)
            val errorMessageBuilder = StringBuilder(messageHeader)
            errorMessageBuilder.appendLine(createReferenceChain(errorModel.referenceChain))
            if (errorModel.propertyName != "") {
                errorMessageBuilder.appendLine(missingElementMessage.format(errorModel.propertyName))
            }
            errorMessageBuilder.appendLine(atMessage.format(errorModel.line, errorModel.column))
            yamlTreeNode?.let {
                errorMessageBuilder.appendLine(errorNodeMessage.format(resolveErrorNode(yamlTreeNode, errorModel)))
            }
            errorMessageBuilder.toString().trim()
        } catch (error: Exception) {
            exceptionTemplate.format(errorMessage)
        }

    operator fun invoke(yamlException: MarkedYAMLException): String {
        val problemMark = yamlException.problemMark
        return StringBuilder(messageHeader + yamlException.problem).apply {
            appendLine()
            appendLine(atMessage.format(problemMark.line, problemMark.column))
            appendLine(errorNodeMessage.format(System.lineSeparator() + problemMark._snippet))
        }.toString().trim()
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
