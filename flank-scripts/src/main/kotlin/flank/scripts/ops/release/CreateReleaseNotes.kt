package flank.scripts.ops.release

import com.github.kittinunf.result.map
import com.github.kittinunf.result.success
import flank.common.withNewLineAtTheEnd
import flank.scripts.data.github.getLatestReleaseTag
import flank.scripts.ops.common.ReleaseNotesWithType
import flank.scripts.ops.common.asString
import flank.scripts.ops.common.generateReleaseNotes
import kotlinx.coroutines.runBlocking
import java.io.File

fun createReleaseNotes(token: String, releaseNotesFilePath: String) = runBlocking {
    getLatestReleaseTag(token)
        .map { it.tag }
        .success { previousTag ->
            File(releaseNotesFilePath).appendReleaseNotes(
                releaseNotesWithType = generateReleaseNotes(previousTag, token),
                releaseTag = generateNextReleaseTag(previousTag)
            )
        }
}

internal fun File.appendReleaseNotes(releaseNotesWithType: ReleaseNotesWithType, releaseTag: String) {
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
