package ftl.domain

import ftl.args.yml.DEVICES_NODE
import ftl.args.yml.GCLOUD_NODE
import ftl.args.yml.VERSION_NODE

data class DoctorErrors(
    val parsingErrors: List<String> = emptyList(),
    val topLevelUnknownKeys: List<String> = emptyList(),
    val nestedUnknownKeys: Map<String, List<String>> = emptyMap(),
    val invalidDevices: List<InvalidDevice> = emptyList()
) {
    companion object {
        val EMPTY = DoctorErrors()
    }

    data class InvalidDevice(
        val version: String,
        val model: String
    )
}

fun DoctorErrors.summary(): String =
    if (isEmpty()) "Valid yml file"
    else buildString {
        parsingErrors.forEach { appendLine(it) }
        invalidDevices
            .map { "Warning: Version should be string $GCLOUD_NODE -> $DEVICES_NODE[${it.model}] -> $VERSION_NODE[${it.version}]" }
            .forEach { appendLine(it) }
        if (topLevelUnknownKeys.isNotEmpty()) appendLine("Unknown top level keys: $topLevelUnknownKeys")
        nestedUnknownKeys.forEach { (topLevelKey, keys) ->
            appendLine("Unknown keys in $topLevelKey -> $keys")
        }
    }.trim()

fun DoctorErrors.isEmpty() =
    parsingErrors.isEmpty() && topLevelUnknownKeys.isEmpty() &&
        nestedUnknownKeys.isEmpty() && invalidDevices.isEmpty()

operator fun DoctorErrors.plus(right: DoctorErrors) = DoctorErrors(
    parsingErrors = parsingErrors.plus(right.parsingErrors).distinct(),
    topLevelUnknownKeys = topLevelUnknownKeys + right.topLevelUnknownKeys,
    nestedUnknownKeys = nestedUnknownKeys + right.nestedUnknownKeys,
    invalidDevices = invalidDevices + right.invalidDevices
)
