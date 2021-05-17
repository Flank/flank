package ftl.domain

import ftl.args.IosArgs
import ftl.doctor.validateYaml
import ftl.presentation.Output
import ftl.presentation.cli.firebase.test.processValidation
import ftl.run.exception.YmlValidationError
import java.nio.file.Paths

interface RunDoctorIos : Output {
    val configPath: String
    val fix: Boolean
}

operator fun RunDoctorIos.invoke() {
    val validationResult = validateYaml(IosArgs, Paths.get(configPath))
    validationResult.out()
    if (fix) processValidation(Paths.get(configPath))
    if (!validationResult.isEmpty()) throw YmlValidationError()
}
