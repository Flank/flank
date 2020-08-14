package flank.scripts.exceptions

import com.google.common.truth.Truth.assertThat
import flank.scripts.github.GitHubErrorResponse
import flank.scripts.release.updatebugsnag.BugSnagResponse
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

    @Test
    fun `Should return correct description of bugnsag exception`() {
        // given
        val testBody = BugSnagResponse("error", errors = listOf("test", "not good"))
        val expectedToString = "Error while doing Bugnsag request, because of test, not good"

        // when
        val actual = BugsnagException(testBody)

        // then
        assertThat(actual.toString()).isEqualTo(expectedToString)
    }
}
