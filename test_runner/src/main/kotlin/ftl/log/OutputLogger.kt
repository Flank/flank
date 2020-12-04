package ftl.log

fun setLogLevel(logLevel: OutputLogLevel) {
    minimumLogLevel = logLevel
}

fun log(message: Any, level: OutputLogLevel = OutputLogLevel.LOW) =
    if (minimumLogLevel >= level) print(message)
    else Unit

fun logLn(message: Any = "", level: OutputLogLevel = OutputLogLevel.LOW) =
    if (minimumLogLevel >= level) println(message)
    else Unit

private var minimumLogLevel: OutputLogLevel = OutputLogLevel.LOW

enum class OutputLogLevel {
    LOW,
    All
}
