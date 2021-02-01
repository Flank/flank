package flank.scripts.ops.ci

import com.github.kittinunf.result.map
import com.github.kittinunf.result.success
import flank.scripts.github.getLatestReleaseTag
import flank.scripts.ops.ci.releasenotes.appendReleaseNotes
import flank.scripts.ops.ci.releasenotes.generateReleaseNotes
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
