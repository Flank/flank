package flank.scripts.ci.releasenotes

import flank.scripts.utils.markdownH2
import java.io.File

fun File.appendReleaseNotes(messages: List<String>, releaseTag: String) {
    appendToReleaseNotes(
        messages = messages,
        releaseTag = releaseTag
    )
}

private fun File.appendToReleaseNotes(messages: List<String>, releaseTag: String) {
    val currentFileLines = readLines()
    val newLines = mutableListOf<String>().apply {
        add(releaseTag.markdownH2())
        addAll(messages)
        add("")
    }

    writeText((newLines + currentFileLines).joinToString(System.lineSeparator()).withNewLineAtTheEnd())
}

private fun String.withNewLineAtTheEnd() = plus(System.lineSeparator())
