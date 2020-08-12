package flank.scripts.ci.releasenotes

import com.google.common.truth.Truth.assertThat
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import org.junit.Test

class AppendReleaseNotesTest {

    @Test
    fun `Should not append to file pr mapper returns null`() {
        // given
        val testFile = mockk<File>()

        // when
        testFile.appendReleaseNotes(1, "ddd", "user")

        // then
        verify { testFile wasNot called }
    }

    @Test
    fun `Should append release note and header to file`() {
        // given
        val testFile = File.createTempFile("empty", ".md")
        val expectedHeader = "## next (unreleased)"
        val expectedReleaseNote =
            "- [#11](https://github.com/Flank/flank/pull/11) **New feature**: Tests ([test_of](https://github.com/test_of))"
        val expectedLinesCount = testFile.readLines().size + 2

        // when
        testFile.appendReleaseNotes(11, "feat: Tests", "test_of")

        // verify
        val lines = testFile.readLines()
        lines.forEach {
            println(it)
        }
        assertThat(lines[0]).isEqualTo(expectedHeader)
        assertThat(lines[1]).isEqualTo(expectedReleaseNote)
        assertThat(lines.size).isEqualTo(expectedLinesCount)
    }

    @Test
    fun `Should append release note to file`() {
        // given
        val testFile = File.createTempFile("empty_with_header", ".md").apply {
            writeText("## next (unreleased)")
        }
        val expectedReleaseNote =
            "- [#11](https://github.com/Flank/flank/pull/11) **New feature**: Tests ([test_of](https://github.com/test_of))"
        val expectedLinesCount = testFile.readLines().size + 1

        // when
        testFile.appendReleaseNotes(11, "feat: Tests", "test_of")

        // verify
        val lines = testFile.readLines()
        assertThat(lines[1]).isEqualTo(expectedReleaseNote)
        assertThat(lines.size).isEqualTo(expectedLinesCount)
    }

    @Test
    fun `Should not append release note if already exist`() {
        // given
        val testFile = File.createTempFile("empty_with_header", ".md").apply {
            writeText(
                "## next (unreleased)" +
                    System.lineSeparator() +
                    "- [#11](https://github.com/Flank/flank/pull/11) **New feature**: Tests ([test_of](https://github.com/test_of))"
            )
        }
        val expectedLinesCount = testFile.readLines().size

        // when
        testFile.appendReleaseNotes(11, "feat: Tests", "test_of")

        // verify
        val lines = testFile.readLines()
        assertThat(lines.size).isEqualTo(expectedLinesCount)
    }
}
