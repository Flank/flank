package ftl.args.yml

import ftl.run.exception.FlankGeneralError

enum class Type(val ymlName: String) {
    INSTRUMENTATION("instrumentation"), ROBO("robo"), GAMELOOP("game-loop");

    companion object {
        fun fromString(stringVal: String): Type {
            val filtered = values().filter { it.ymlName == stringVal }
            if (filtered.isEmpty()) {
                throw FlankGeneralError("Unsupported Type given `$stringVal` only [${values().joinToString(",")}] supported.")
            }
            return filtered.first()
        }
    }
}
