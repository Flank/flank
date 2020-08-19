package flank.scripts.ci.releasenotes

import com.google.common.truth.Truth.assertThat
import flank.scripts.utils.markdownH2
import flank.scripts.utils.markdownH3
import java.io.File
import org.junit.Test

class AppendReleaseNotesTest {

    @Test
    fun `Should append release note and header to file`() {
        // given
        val testFile = File.createTempFile("empty", ".md")
        val tag = "v20.08.0"
        val expectedHeader = tag.markdownH2()
        val messages = mapOf(
            "Features" to listOf(
                "- [#1](https://github.com/Flank/flank/pull/1) Tests1 ([test_of](https://github.com/test_of))",
                "- [#2](https://github.com/Flank/flank/pull/2) Tests2 ([test_of](https://github.com/test_of))",
                "- [#3](https://github.com/Flank/flank/pull/3) Tests3 ([test_of](https://github.com/test_of))"
            ),
            "Bug fixes" to listOf(
                "- [#11](https://github.com/Flank/flank/pull/11) Tests11 ([test_of](https://github.com/test_of))",
                "- [#12](https://github.com/Flank/flank/pull/12) Tests12 ([test_of](https://github.com/test_of))",
                "- [#13](https://github.com/Flank/flank/pull/13) Tests13 ([test_of](https://github.com/test_of))"
            ),
            "Documentation" to listOf(
                "- [#21](https://github.com/Flank/flank/pull/21) Tests21 ([test_of](https://github.com/test_of))",
                "- [#22](https://github.com/Flank/flank/pull/22) Tests22 ([test_of](https://github.com/test_of))",
                "- [#23](https://github.com/Flank/flank/pull/23) Tests33 ([test_of](https://github.com/test_of))"
            )
        )
        val expectedLinesCount = testFile.readLines().size +
            12 + // values to append
            3 // header + newline + end newline

        // when
        testFile.appendReleaseNotes(messages, tag)

        // verify
        val lines = testFile.readLines()
        assertThat(lines.first()).isEqualTo(expectedHeader)
        var currentLine = 1
        messages.keys.forEach { type ->
            assertThat(lines[currentLine++]).isEqualTo(type.markdownH3())
            messages[type]?.forEach { line ->
                assertThat(lines[currentLine++]).isEqualTo(line)
            }
        }
        assertThat(lines.last()).isEmpty()
        assertThat(lines.size).isEqualTo(expectedLinesCount)
    }
}
