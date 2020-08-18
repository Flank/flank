package flank.scripts.utils

import com.google.common.truth.Truth.assertThat
import flank.scripts.github.GitHubErrorResponse
import flank.scripts.release.updatebugsnag.SourceControl
import org.junit.Test

class SerializationTest {

    @Test
    fun `Should create object from json`() {
        // given
        val testJson = """
            {
              "message": "Bad credentials",
              "documentation_url": "https://developer.github.com/v3"
            }
        """.trimIndent()
        val expected = GitHubErrorResponse(
            "Bad credentials",
            "https://developer.github.com/v3"
        )

        // when
        val actual = testJson.toObject(GitHubErrorResponse.serializer())

        // then
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Should create json from object`() {
        // given
        val testObject = SourceControl("a", "b", "c")
        val expected = "{\"provider\":\"a\",\"repository\":\"b\",\"revision\":\"c\"}"

        // when
        val actual = testObject.toJson(SourceControl.serializer())

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
