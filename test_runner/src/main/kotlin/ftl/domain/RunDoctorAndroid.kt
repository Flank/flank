package ftl.domain

import ftl.args.AndroidArgs
import ftl.doctor.validateYaml
import ftl.presentation.Output
import ftl.presentation.cli.firebase.test.processValidation
import java.nio.file.Paths

interface RunDoctorAndroid : Output {
    val configPath: String
    val fix: Boolean
}

operator fun RunDoctorAndroid.invoke() {
    val ymlPath = Paths.get(configPath)
    val validationResult = validateYaml(AndroidArgs, ymlPath)
    processValidation(fix, ymlPath)
    validationResult.out()
}
