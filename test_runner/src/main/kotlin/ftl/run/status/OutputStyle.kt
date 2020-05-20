package ftl.run.status

import ftl.util.FlankFatalError

enum class OutputStyle { Verbose, Single, Multi }

fun String.asOutputStyle() = capitalize().let { capitalized ->
    OutputStyle.values().find { style -> style.name == capitalized }
} ?: throw FlankFatalError("Cannot parse output-style: $this, it should be one of ${OutputStyle.values().map { it.name.toLowerCase() }}")
