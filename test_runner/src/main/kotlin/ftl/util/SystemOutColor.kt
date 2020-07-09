package ftl.util

enum class SystemOutColor(private val ansiCode: String) {
    DEFAULT("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m");

    fun applyTo(value: String) = ansiCode + value + DEFAULT.ansiCode

    val additionalLengthWhenApplied get() = ansiCode.length + DEFAULT.ansiCode.length
}
