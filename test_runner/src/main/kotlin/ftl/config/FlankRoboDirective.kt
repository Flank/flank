package ftl.config

import ftl.util.trimStartLine

data class FlankRoboDirective(
    val type: String,
    val name: String,
    val input: String? = null // Input is only permitted for text type elements
) {
    override fun toString() = """
        - type: $type
          name: $name
          input: $input""".trimStartLine()
}

fun List<String>.parseRoboDirectives() = map(String::parseRoboDirective)

fun String.parseRoboDirective(): FlankRoboDirective = split(
    Regex("([:=])")
).let { chunks ->
    require(chunks.size == 3) {
        "Cannot parse robo directive `$this`, use following format `\$TYPE:\$RESOURCE_NAME=\$INPUT`"
    }
    FlankRoboDirective(
        type = chunks[0],
        name = chunks[1],
        input = chunks[2]
    )
}
