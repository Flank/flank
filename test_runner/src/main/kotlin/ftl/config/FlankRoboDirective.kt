package ftl.config

data class FlankRoboDirective(
    val type: String,
    val name: String,
    val input: String = "" // Input is only permitted for text type elements
) {
    override fun toString() = "        \"$type:$name\": $formattedInput"

    private val formattedInput
        get() = if (input.isNotBlank()) input else "\"\""
}

fun List<String>.parseRoboDirectives() = map {
    val (type, name, input) = it.split(Regex("([:=])"))
    FlankRoboDirective(type, name, input)
}

fun Map<String, String>.parseRoboDirectives() = map { (key, input) ->
    val (type, name) = key.split(":")
    FlankRoboDirective(type, name, input)
}
