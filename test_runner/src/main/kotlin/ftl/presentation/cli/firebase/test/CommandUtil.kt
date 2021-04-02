package ftl.presentation.cli.firebase.test

import flank.common.logLn
import ftl.args.yml.YamlDeprecated
import ftl.args.yml.fixDevices
import ftl.run.exception.YmlValidationError
import java.nio.file.Path

fun processValidation(validationResult: String, shouldFix: Boolean, ymlPath: Path) {
    when {
        validationResult.isBlank() -> logLn("Valid yml file")
        !shouldFix -> {
            logLn(validationResult)
            throw YmlValidationError("Invalid yml file, use --fix for automatically fix yml")
        }
        else -> {
            logLn(validationResult)
            logLn("Trying to fix yml file")
            if (YamlDeprecated.modify(ymlPath)) {
                throw YmlValidationError("Invalid yml file, unable to fix yml file")
            }
            fixDevices(ymlPath)
        }
    }
}
