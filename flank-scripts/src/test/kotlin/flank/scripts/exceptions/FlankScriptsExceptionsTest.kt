package flank.scripts.exceptions

import com.google.common.truth.Truth.assertThat
import flank.scripts.github.GitHubErrorResponse
import org.junit.Test

class FlankScriptsExceptionsTest {

    @Test
    fun `Should return correct description of github exception`() {
        // given
        val testBody = GitHubErrorResponse("error", "www.error.test")
        val expectedToString = "Error while doing GitHub request, because of error, more info at www.error.test"

        // when
        val actual = GitHubException(testBody)

        // then
        assertThat(actual.toString()).isEqualTo(expectedToString)
    }
}
