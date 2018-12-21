package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.json.SavedMatrix
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ArtifactRegexTest {

    private companion object {
        const val TO_DOWNLOAD_1 = "/sdcard/screenshots"
        const val TO_DOWNLOAD_2 = "/sdcard/test"

        val TO_DOWNLOAD_REGEX_1 = Regex("^\\Q2018-12-21_11:24:44.608000_UpOC/shard_0\\E/.*/artifacts\\Q/sdcard/screenshots\\E/.*")
        val TO_DOWNLOAD_REGEX_2 = Regex("^\\Q2018-12-21_11:24:44.608000_UpOC/shard_0\\E/.*/artifacts\\Q/sdcard/test\\E/.*")

        const val MATRIX_GCS_PATH_WITHOUT_ROOT_BUCKET = "2018-12-21_11:24:44.608000_UpOC/shard_0"
    }

    private val matrix = mock(SavedMatrix::class.java)

    @Test
    fun regexExists() {
        assertThat(ArtifactRegex.testResultRgx).isNotNull()
        assertThat(ArtifactRegex.screenshotRgx).isNotNull()
    }

    @Test
    fun `artifactsToDownload should return just testResult and screenshot regex if no directoriesToDownload`() {
        assertRegexEquals(ArtifactRegex.artifactsToDownload(matrix, emptyList()),
            listOf(ArtifactRegex.testResultRgx, ArtifactRegex.screenshotRgx))
    }

    @Test
    fun `artifactsToDownload should return testResult, screenshot regex and directoriesToDownload regex if one`() {
        `when`(matrix.gcsPathWithoutRootBucket).thenReturn(MATRIX_GCS_PATH_WITHOUT_ROOT_BUCKET)

        assertRegexEquals(ArtifactRegex.artifactsToDownload(matrix, listOf(TO_DOWNLOAD_1)),
            listOf(ArtifactRegex.testResultRgx, ArtifactRegex.screenshotRgx, TO_DOWNLOAD_REGEX_1))
    }

    @Test
    fun `artifactsToDownload should return testResult, screenshot regex and directoriesToDownload regex if multiple`() {
        `when`(matrix.gcsPathWithoutRootBucket).thenReturn(MATRIX_GCS_PATH_WITHOUT_ROOT_BUCKET)

        assertRegexEquals(ArtifactRegex.artifactsToDownload(matrix, listOf(TO_DOWNLOAD_1, TO_DOWNLOAD_2)),
            listOf(ArtifactRegex.testResultRgx, ArtifactRegex.screenshotRgx, TO_DOWNLOAD_REGEX_1, TO_DOWNLOAD_REGEX_2))
    }

    private fun assertRegexEquals(expected: List<Regex>, actual: List<Regex>) {
        assertThat(actual.map { it.pattern })
            .containsExactly(*(expected.map { it.pattern }.toTypedArray()))
    }
}
