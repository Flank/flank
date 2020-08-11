package flank.scripts.ci.releasenotes

import java.io.File

fun File.appendReleaseNotes(prNumber: Int, prTitle: String, gitUser: String) {
    val changeLogTitle = prTitle.mapPrTitle() ?: return

    takeUnless { it.containsIssue(prNumber) }
            ?.appendToUnreleased("${prNumberFormat(prNumber)} $changeLogTitle ${gitUserFormat(gitUser)}")
}

private fun File.containsIssue(prNumber: Int) = readLines().any { line -> line.startsWith(prNumberFormat(prNumber)) }

private fun File.appendToUnreleased(message: String) {
    val lines = readLines().toMutableList()
    if (!startsWithUnreleased()) {
        lines.add(0, UNRELEASED_MARKDOWN_LINE)
    }

    lines.add(
            index = lines.indexOfFirst { it.isBlank() }.takeIf { it > -1 } ?: 1,
            element = message
    )
    writeText(lines.joinToString(System.lineSeparator()).withNewLineAtEnd())
}

private fun File.startsWithUnreleased() = readLines().firstOrNull() == UNRELEASED_MARKDOWN_LINE

private fun String.withNewLineAtEnd() = plus(System.lineSeparator())

private const val UNRELEASED_MARKDOWN_LINE = "## next (unreleased)"

private val prNumberFormat: (Int) -> String = { pr -> "- [#$pr](https://github.com/Flank/flank/pull/$pr)" }
private val gitUserFormat: (String) -> String = { gitUser -> "([$gitUser](https://github.com/$gitUser))" }
