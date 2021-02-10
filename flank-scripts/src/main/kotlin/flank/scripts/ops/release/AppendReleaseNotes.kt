package flank.scripts.ops.release

import flank.common.withNewLineAtTheEnd
import flank.scripts.ops.common.ReleaseNotesWithType
import flank.scripts.ops.common.asString
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
