package flank.scripts.ci.releasenotes

import com.google.common.truth.Truth.assertThat
import flank.scripts.utils.markdownH2
import java.io.File
import org.junit.Test

class AppendReleaseNotesTest {

    @Test
    fun `Should append release note and header to file`() {
        // given
        val testFile = File.createTempFile("empty", ".md")
        val tag = "v20.08.0"
        val expectedHeader = tag.markdownH2()
        val messages = listOf(
            "- [#11](https://github.com/Flank/flank/pull/11) **New feature**: Tests ([test_of](https://github.com/test_of))",
            "- [#12](https://github.com/Flank/flank/pull/12) **New feature**: Tests2 ([test_of](https://github.com/test_of))",
            "- [#13](https://github.com/Flank/flank/pull/13) **New feature**: Tests3 ([test_of](https://github.com/test_of))"
        )
        val expectedLinesCount = testFile.readLines().size + messages.size + 2 // header + newline

        // when
        testFile.appendReleaseNotes(messages, tag)

        // verify
        val lines = testFile.readLines()
        assertThat(lines.first()).isEqualTo(expectedHeader)
        messages.forEachIndexed { index, text ->
            assertThat(lines[index + 1]).isEqualTo(text)
        }
        assertThat(lines.last()).isEmpty()
        assertThat(lines.size).isEqualTo(expectedLinesCount)
    }
}
