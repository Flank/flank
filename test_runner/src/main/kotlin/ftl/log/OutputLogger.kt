package ftl.log

fun setLogLevel(logLevel: OutputLogLevel) {
    minimumLogLevel = logLevel
}

fun log(message: Any, level: OutputLogLevel = OutputLogLevel.SIMPLE) =
    if (minimumLogLevel >= level) print(message)
    else Unit

fun logLn(message: Any = "", level: OutputLogLevel = OutputLogLevel.SIMPLE) =
    if (minimumLogLevel >= level) println(message)
    else Unit

private var minimumLogLevel: OutputLogLevel = OutputLogLevel.DETAILED

enum class OutputLogLevel {
    SIMPLE,
    DETAILED
}
