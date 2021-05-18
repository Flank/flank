package ftl.domain

import ftl.args.AndroidArgs
import ftl.doctor.validateYaml
import ftl.presentation.Output
import ftl.presentation.cli.firebase.test.processValidation
import ftl.run.exception.YmlValidationError
import java.nio.file.Paths

interface RunDoctorAndroid : Output {
    val configPath: String
    val fix: Boolean
}

operator fun RunDoctorAndroid.invoke() {
    val validationResult = validateYaml(AndroidArgs, Paths.get(configPath))
    if (fix) processValidation(Paths.get(configPath))
    validationResult.out()
    if (!validationResult.isEmpty()) throw YmlValidationError()
}
