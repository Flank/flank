package flank.scripts.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MarkdownFormatterTest {

    @Test
    fun `Should properly applied markdown bold`() {
        // given
        val testString = "text"

        // when
        val actual = testString.markdownBold()

        // then
        assertThat(actual).isEqualTo("**$testString**")
    }

    @Test
    fun `Should properly apply markdown link`() {
        // given
        val testString = "text"
        val testUrl = "www.flank.com"

        // when
        val actual = markdownLink(testString, testUrl)

        // then
        assertThat(actual).isEqualTo("[$testString]($testUrl)")
    }

    @Test
    fun `Should properly apply markdown h2`() {
        // given
        val testString = "text"

        // when
        val actual = testString.markdownH2()

        // then
        assertThat(actual).isEqualTo("## $testString")
    }
}
