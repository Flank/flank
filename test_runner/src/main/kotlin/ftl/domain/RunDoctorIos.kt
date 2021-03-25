package ftl.domain

import ftl.args.IosArgs
import ftl.cli.firebase.test.processValidation
import ftl.doctor.validateYaml
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
