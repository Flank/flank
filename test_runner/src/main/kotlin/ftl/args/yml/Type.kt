package ftl.args.yml

import ftl.run.exception.FlankGeneralError

enum class Type(val ymlName: String) {
    INSTRUMENTATION("instrumentation"),
    ROBO("robo"),
    XCTEST("xctest"),
    GAMELOOP("game-loop");
}

fun String.toType() = Type.values().find { it.ymlName == this } ?: throwUnsuportedType()

private fun String.throwUnsuportedType(): Nothing {
    throw FlankGeneralError("Unsupported Type given `$this` only [${Type.values().joinToString(",") { it.ymlName }}] supported.")
}
