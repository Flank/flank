package ftl.presentation.cli.firebase.test

import flank.common.logLn
import ftl.args.yml.YamlDeprecated
import ftl.args.yml.fixDevices
import ftl.run.exception.YmlValidationError
import java.nio.file.Path

fun processValidation(shouldFix: Boolean, ymlPath: Path) {
    if (shouldFix) {
        logLn("Trying to fix yml file")
        if (YamlDeprecated.modify(ymlPath)) {
            throw YmlValidationError("Invalid yml file, unable to fix yml file")
        }
        fixDevices(ymlPath)
    }
}
