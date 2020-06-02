package ftl.args.yml

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.error.MarkedYAMLException
import ftl.args.yml.errors.ConfigurationErrorMessageBuilder
import ftl.util.FlankFatalError

fun convertConfigurationErrorExceptions(missingParameterError: Exception, yaml: JsonNode?): Throwable {
    val errorMessageBuilder = ConfigurationErrorMessageBuilder
    val errorMessage = missingParameterError.message
    return if (errorMessage != null) {
        FlankFatalError(errorMessageBuilder(errorMessage, yaml))
    } else {
        missingParameterError
    }
}

fun convertConfigurationErrorExceptions(treeParsingException: MarkedYAMLException): Throwable {
    val errorMessageBuilder = ConfigurationErrorMessageBuilder
    return FlankFatalError(errorMessageBuilder(treeParsingException))
}
