package ftl.util

enum class SystemOutColor(val ansiCode: String) {
    DEFAULT("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m");

    fun applyTo(value: String) = ansiCode + value + DEFAULT.ansiCode

    val additionalLengthWhenApplied get() = ansiCode.length + DEFAULT.ansiCode.length
}
