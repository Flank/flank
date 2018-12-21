package ftl.util

import com.google.common.annotations.VisibleForTesting
import ftl.json.SavedMatrix

object ArtifactRegex {

    @VisibleForTesting
    val testResultRgx = Regex(".*test_result_\\d+\\.xml$")
    @VisibleForTesting
    val screenshotRgx = Regex(".*\\.png$")

    /**
     * @return a list of regex's that should be downloaded
     */
    fun artifactsToDownload(matrix: SavedMatrix, directoriesToDownload: List<String>): List<Regex> {
        val directoriesRegexToDownload = directoriesToDownload
            .map { "^${Regex.escape(matrix.gcsPathWithoutRootBucket)}/.*/artifacts${Regex.escape(it)}/.*" }
            .map { Regex(it) }
        return listOf(testResultRgx, screenshotRgx) + directoriesRegexToDownload
    }
}
