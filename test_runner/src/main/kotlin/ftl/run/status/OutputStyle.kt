package ftl.run.status

import ftl.run.exception.FlankConfigurationError

enum class OutputStyle { Verbose, Single, Multi, Compact }

fun String.asOutputStyle() = replaceFirstChar { it.uppercase() }.let { capitalized ->
    OutputStyle.values().find { style -> style.name == capitalized }
} ?: throw FlankConfigurationError(
    "Cannot parse output-style: $this, it should be one of ${OutputStyle.values().map { it.name.lowercase() }}"
)
