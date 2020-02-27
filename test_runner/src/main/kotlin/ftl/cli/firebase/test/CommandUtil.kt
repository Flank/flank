package ftl.cli.firebase.test

import ftl.args.yml.YamlDeprecated
import java.nio.file.Path
import kotlin.system.exitProcess

fun processValidation(validationResult: String, shouldFix: Boolean, ymlPath: Path) {
    when {
        validationResult.isBlank() -> println("Valid yml file")
        !shouldFix -> {
            println(validationResult)
            exitProcess(1)
        }
        else -> {
            println(validationResult)
            println("Trying to fix yml file")
            if (YamlDeprecated.modify(ymlPath)) {
                println("Unable to fix yml file")
                exitProcess(1)
            }
        }
    }
}
