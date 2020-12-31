package flank.common

import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths

val newLine: String
    get() = System.lineSeparator()

fun String.withNewLineAtTheEnd() = plus(System.lineSeparator())

fun String.startWithNewLine() = System.lineSeparator() + this

fun String.normalizeLineEnding(): String {
    // required for tests to pass on Windows
    return this.replace("\r\n", "\n")
}

fun String.trimStartLine(): String {
    return this.split("\n").drop(1).joinToString("\n")
}

fun StringWriter.println(msg: String = "") {
    appendLine(msg)
}

fun String.write(data: String) {
    Files.write(Paths.get(this), data.toByteArray())
}

fun join(first: String, vararg more: String): String {
    // Note: Paths.get(...) does not work for joining because the path separator
    // will be '\' on Windows which is invalid for a URI
    return listOf(first, *more)
        .joinToString("/")
        .replace("\\", "/")
        .replace(regex = Regex("/+"), replacement = "/")
}
