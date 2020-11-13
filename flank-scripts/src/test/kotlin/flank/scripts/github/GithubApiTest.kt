package flank.scripts.github

import com.github.kittinunf.result.Result
import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import flank.scripts.ci.releasenotes.GitHubRelease
import flank.scripts.exceptions.GitHubException
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class GithubApiTest {

    @Test
    fun `Should return success for correct delete tag request call`() {
        // when
        val actual = deleteOldTag("success", "user", "password")

        // then
        assertThat(actual).isInstanceOf(Result.Success::class.java)
    }

    @Test
    fun `Should return failure for incorrect delete tag request call`() {
        // when
        val actual = deleteOldTag("failure", "user", "password")

        // then
        assertThat(actual).isInstanceOf(Result.Failure::class.java)
        val (_, exception) = actual
        assertThat(exception).isInstanceOf(GitHubException::class.java)
    }

    @Test
    fun `Should return success for correct get latest tag request call`() {
        runBlocking {
            // when
            val actual = getLatestReleaseTag("success")

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response).isInstanceOf(GitHubRelease::class.java)
        }
    }

    @Test
    fun `Should return failure for incorrect get latest tag request call`() {
        runBlocking {
            // when
            val actual = getLatestReleaseTag("failure")

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `Should return success for correct pr details by commit request call`() {
        runBlocking {
            // when
            val actual = getPrDetailsByCommit("success", "success")

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response?.first()).isInstanceOf(GithubPullRequest::class.java)
        }
    }

    @Test
    fun `Should return failure for incorrect pr details by commit request call`() {
        runBlocking {
            // when
            val actual = getPrDetailsByCommit("sha", "failure")

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `Should return failure for incorrect get pull request result`() {
        runBlocking {
            // when
            val actual = getGitHubPullRequest("failure", 0)

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `Should return success for correct get pull request result`() {
        runBlocking {
            // when
            val actual = getGitHubPullRequest("success", 1)

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response).isInstanceOf(GithubPullRequest::class.java)
        }
    }
}
