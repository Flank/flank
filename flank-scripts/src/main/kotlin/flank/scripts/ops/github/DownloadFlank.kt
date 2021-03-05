package flank.scripts.ops.github

import com.github.kittinunf.result.map
import com.google.common.annotations.VisibleForTesting
import flank.common.downloadFile
import flank.scripts.data.github.getLatestReleaseTag

suspend fun downloadFlank(version: String?) =
    version.getVersion().prepareDownloadUrl().downloadFlank()

@VisibleForTesting
internal suspend fun String?.getVersion(): String =
    takeUnless { it.isNullOrBlank() } ?: getLatestReleaseTag("").map { it.tag }.get()

private fun String.prepareDownloadUrl() = "https://github.com/Flank/flank/releases/download/$this/$FLANK_JAR"

private fun String.downloadFlank() = downloadFile(this, FLANK_JAR)

private const val FLANK_JAR = "flank.jar"
