package ftl.cli.firebase.test

import ftl.args.yml.YamlDeprecated
import ftl.args.yml.fixDevices
import ftl.log.logLine
import ftl.run.exception.YmlValidationError
import java.nio.file.Path

fun processValidation(validationResult: String, shouldFix: Boolean, ymlPath: Path) {
    when {
        validationResult.isBlank() -> logLine("Valid yml file")
        !shouldFix -> {
            logLine(validationResult)
            throw YmlValidationError("Invalid yml file, use --fix for automatically fix yml")
        }
        else -> {
            logLine(validationResult)
            logLine("Trying to fix yml file")
            if (YamlDeprecated.modify(ymlPath)) {
                throw YmlValidationError("Invalid yml file, unable to fix yml file")
            }
            fixDevices(ymlPath)
        }
    }
}
