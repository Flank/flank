@file:Suppress("DEPRECATION")

package ftl.args.yml

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException
import ftl.args.yml.errors.ConfigurationErrorMessageBuilder
import ftl.run.exception.FlankConfigurationError

fun convertConfigurationErrorExceptions(missingParameterError: Exception, yaml: JsonNode?): Throwable {
    val errorMessageBuilder = ConfigurationErrorMessageBuilder
    val errorMessage = missingParameterError.message
    return if (errorMessage != null) {
        FlankConfigurationError(errorMessageBuilder(errorMessage, yaml))
    } else {
        missingParameterError
    }
}

fun convertConfigurationErrorExceptions(treeParsingException: MarkedYAMLException): Throwable {
    return FlankConfigurationError(ConfigurationErrorMessageBuilder(treeParsingException))
}
