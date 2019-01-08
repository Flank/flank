package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.args.IArgs
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class ArtifactsTest {

    private companion object {
        const val sdcardScreenshots = "/sdcard/screenshots"
        const val sdcardTest = "/sdcard/test"
        val sdcardScreenshotsRgx = Regex("/sdcard/screenshots")
        val sdcardTestRgx = Regex("/sdcard/test")
    }

    private fun createDirs(dirsToDownload: List<String> = emptyList()): IArgs {
        val mock = mock(IArgs::class.java)
        `when`(mock.filesToDownload).thenReturn(dirsToDownload)
        return mock
    }

    @Test
    fun regexExists() {
        assertThat(Artifacts.testResultRgx).isNotNull()
    }

    @Test
    fun `artifactsToDownload should return just testResult and screenshot regex if no filesToDownload`() {
        assertRegexEquals(
            Artifacts.regexList(createDirs()),
            listOf(Artifacts.testResultRgx)
        )
    }

    @Test
    fun `artifactsToDownload should return testResult, screenshot regex and filesToDownload regex if one`() {
        val actual = Artifacts.regexList(createDirs(listOf(sdcardScreenshots)))
        val expected = listOf(Artifacts.testResultRgx, sdcardScreenshotsRgx)
        assertRegexEquals(actual, expected)
    }

    @Test
    fun `artifactsToDownload should return testResult, screenshot regex and filesToDownload regex if multiple`() {
        val actual = Artifacts.regexList(createDirs(listOf(sdcardScreenshots, sdcardTest)))
        val expected = listOf(Artifacts.testResultRgx, sdcardScreenshotsRgx, sdcardTestRgx)
        assertRegexEquals(actual, expected)
    }

    private fun assertRegexEquals(expected: List<Regex>, actual: List<Regex>) {
        assertThat(actual.map { it.pattern })
            .containsExactly(*(expected.map { it.pattern }.toTypedArray()))
    }
}
