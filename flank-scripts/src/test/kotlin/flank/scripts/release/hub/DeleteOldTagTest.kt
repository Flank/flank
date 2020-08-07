package flank.scripts.release.hub

import com.github.kittinunf.result.Result
import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import flank.scripts.exceptions.GitHubException
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class DeleteOldTagTest {

    @Test
    fun `Should return success for correct request call`() {
        // when
        val actual = deleteOldTag("success", "user", "password")

        // then
        assertThat(actual).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `Should return failure for incorrect request call`() {
        // when
        val actual = deleteOldTag("failure", "user", "password")

        // then
        assertThat(actual).isInstanceOf(Result.Failure::class.java)
        val (_, exception) = actual
        assertThat(exception).isInstanceOf(GitHubException::class.java)
    }
}
