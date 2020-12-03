package ftl.log

fun setLogLevel(logLevel: OutputLogLevel) {
    minimumLogLevel = logLevel
}

private var minimumLogLevel: OutputLogLevel = OutputLogLevel.LOW

fun log(message: Any) = print(message)

fun logLine(message: Any = "") = println(message)

fun logHigh(message: Any) {
    if (minimumLogLevel >= OutputLogLevel.HIGH) print(message)
}

fun logLineHigh(message: Any = "") {
    if (minimumLogLevel >= OutputLogLevel.HIGH)
        println(message)
}

enum class OutputLogLevel {
    LOW,
    HIGH
}
