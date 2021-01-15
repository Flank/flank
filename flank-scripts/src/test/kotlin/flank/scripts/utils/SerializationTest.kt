package flank.scripts.utils

import com.google.common.truth.Truth.assertThat
import flank.scripts.github.GitHubErrorResponse
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
        val actual = testJson.toObject<GitHubErrorResponse>()

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
