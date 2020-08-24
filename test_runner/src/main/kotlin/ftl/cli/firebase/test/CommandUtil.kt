package ftl.cli.firebase.test

import ftl.args.yml.YamlDeprecated
import ftl.run.exception.YmlValidationError
import java.nio.file.Path

fun processValidation(validationResult: String, shouldFix: Boolean, ymlPath: Path) {
    when {
        validationResult.isBlank() -> println("Valid yml file")
        !shouldFix -> {
            println(validationResult)
            throw YmlValidationError()
        }
        else -> {
            println(validationResult)
            println("Trying to fix yml file")
            if (YamlDeprecated.modify(ymlPath)) {
                println("Unable to fix yml file")
                throw YmlValidationError()
            }
        }
    }
}
