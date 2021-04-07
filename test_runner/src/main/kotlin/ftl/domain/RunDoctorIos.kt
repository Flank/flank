package ftl.domain

import ftl.args.IosArgs
import ftl.doctor.validateYaml
import ftl.presentation.cli.firebase.test.processValidation
import java.nio.file.Paths

interface RunDoctorIos {
    val configPath: String
    val fix: Boolean
}

operator fun RunDoctorIos.invoke() {
    val ymlPath = Paths.get(configPath)
    val validationResult = validateYaml(IosArgs, Paths.get(configPath))
    processValidation(validationResult, fix, ymlPath)
}
