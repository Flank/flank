package ftl.args.yml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException

class YamlObjectMapper : ObjectMapper(YAMLFactory()) {
    override fun <T> readValue(content: String?, valueType: Class<T>?): T {
        try {
            return readValue(content, _typeFactory.constructType(valueType))
        } catch (missingParameterError: MissingKotlinParameterException) {
            throw convertConfigurationErrorExceptions(missingParameterError, readTree(content))
        } catch (mismatchedInputException: MismatchedInputException) {
            throw convertConfigurationErrorExceptions(mismatchedInputException, readTree(content))
        }
    }
}
