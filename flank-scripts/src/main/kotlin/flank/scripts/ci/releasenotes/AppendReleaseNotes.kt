package flank.scripts.ci.releasenotes

import flank.scripts.utils.withNewLineAtTheEnd
import java.io.File

fun File.appendReleaseNotes(releaseNotesWithType: ReleaseNotesWithType, releaseTag: String) {
    appendToReleaseNotes(
        releaseNotesWithType = releaseNotesWithType,
        releaseTag = releaseTag
    )
}

private fun File.appendToReleaseNotes(releaseNotesWithType: ReleaseNotesWithType, releaseTag: String) {
    writeText(
        releaseNotesWithType.asString(releaseTag).withNewLineAtTheEnd() +
  readLines().joinToString(System.lineSeparator()).withNewLineAtTheEnd()
    )
}
