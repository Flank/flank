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

data class DoctorResult(
    val parsingErrors: List<String> = emptyList(),
    val topLevelUnknownKeys: List<String> = emptyList(),
    val nestedUnknownKeys: Map<String, String> = emptyMap(),
    val invalidDevices: List<InvalidDevice> = emptyList()
) {
    data class InvalidDevice(
        val version: String,
        val model: String
    )
}

operator fun DoctorResult.plus(right: DoctorResult) = DoctorResult(
    parsingErrors = parsingErrors + right.parsingErrors,
    topLevelUnknownKeys = topLevelUnknownKeys + right.topLevelUnknownKeys,
    nestedUnknownKeys = nestedUnknownKeys + right.nestedUnknownKeys,
    invalidDevices = invalidDevices + right.invalidDevices
)

fun DoctorResult.isEmpty() =
    parsingErrors.isEmpty() && topLevelUnknownKeys.isEmpty() &&
        nestedUnknownKeys.isEmpty() && invalidDevices.isEmpty()
