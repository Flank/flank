package ftl.domain

import ftl.args.IosArgs
import ftl.doctor.validateYaml
import ftl.presentation.Output
import ftl.presentation.cli.firebase.test.processValidation
import java.nio.file.Paths

interface RunDoctorIos : Output {
    val configPath: String
    val fix: Boolean
}

operator fun RunDoctorIos.invoke() {
    val ymlPath = Paths.get(configPath)
    val validationResult = validateYaml(IosArgs, Paths.get(configPath))
    processValidation(fix, ymlPath)
    validationResult.out()
}
