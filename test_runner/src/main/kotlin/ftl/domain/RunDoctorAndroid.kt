package ftl.domain

import ftl.args.AndroidArgs
import ftl.cli.firebase.test.processValidation
import ftl.doctor.validateYaml
import java.nio.file.Paths

interface RunDoctorAndroid {
    val configPath: String
    val fix: Boolean
}

operator fun RunDoctorAndroid.invoke() {
    val ymlPath = Paths.get(configPath)
    val validationResult = validateYaml(AndroidArgs, ymlPath)
    processValidation(validationResult, fix, ymlPath)
}
