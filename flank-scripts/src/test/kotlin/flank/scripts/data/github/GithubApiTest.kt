package flank.scripts.data.github

import com.github.kittinunf.result.Result
import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import flank.scripts.data.github.objects.GitHubCommit
import flank.scripts.data.github.objects.GitHubCreateIssueCommentRequest
import flank.scripts.data.github.objects.GitHubCreateIssueCommentResponse
import flank.scripts.data.github.objects.GitHubCreateIssueRequest
import flank.scripts.data.github.objects.GitHubCreateIssueResponse
import flank.scripts.data.github.objects.GitHubRelease
import flank.scripts.data.github.objects.GitHubUpdateIssueRequest
import flank.scripts.data.github.objects.GitHubWorkflowRunsSummary
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.data.github.objects.IssueState
import flank.scripts.utils.exceptions.GitHubException
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

    @Test
    fun `Should return failure for incorrect get issue result`() {
        runBlocking {
            // when
            val actual = getGitHubIssue("failure", 0)

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `Should return success for correct get issue result`() {
        runBlocking {
            // when
            val actual = getGitHubIssue("success", 1)

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response).isInstanceOf(GithubPullRequest::class.java)
        }
    }

    @Test
    fun `should return issue list`() {
        runBlocking {
            // when
            val actual = getGitHubIssueList("success", emptyList())

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response?.first()).isInstanceOf(GithubPullRequest::class.java)
        }
    }

    @Test
    fun `should return failure for incorrect issue list result`() {
        runBlocking {
            // when
            val actual = getGitHubIssueList("failure")

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `should return commit list`() {
        runBlocking {
            // when
            val actual = getGitHubCommitList("success")

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response?.first()).isInstanceOf(GitHubCommit::class.java)
        }
    }

    @Test
    fun `should return failure for incorrect commit list result`() {
        runBlocking {
            // when
            val actual = getGitHubCommitList("failure")

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `should return workflow summary for by workflow name`() {
        runBlocking {
            // when
            val actual = getGitHubWorkflowRunsSummary("success", "test-workflow.yml")

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response).isInstanceOf(GitHubWorkflowRunsSummary::class.java)
        }
    }

    @Test
    fun `should return failure for incorrect workflow summary result`() {
        runBlocking {
            // when
            val actual = getGitHubWorkflowRunsSummary("failure", "test-workflow.yml")

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `should create new issue comment and return comment response`() {
        runBlocking {
            // when
            val payload = GitHubCreateIssueCommentRequest("Any body!")
            val actual = postNewIssueComment("success", 123, payload)

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response).isInstanceOf(GitHubCreateIssueCommentResponse::class.java)
        }
    }

    @Test
    fun `should return failure when problem with comment creation occurred`() {
        runBlocking {
            // when
            val payload = GitHubCreateIssueCommentRequest("Any body!")
            val actual = postNewIssueComment("failure", 123, payload)

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `should create new issue and return issue response`() {
        runBlocking {
            // when
            val payload = GitHubCreateIssueRequest(body = "Any body!", title = "Any title")
            val actual = postNewIssue("success", payload)

            // then
            assertThat(actual).isInstanceOf(Result.Success::class.java)
            val (response, _) = actual
            assertThat(response).isInstanceOf(GitHubCreateIssueResponse::class.java)
        }
    }

    @Test
    fun `should return failure when problem with issue creation occurred`() {
        runBlocking {
            // when
            val payload = GitHubCreateIssueRequest(body = "Any body!", title = "Any title")
            val actual = postNewIssue("failure", payload)

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }

    @Test
    fun `should update an issue`() {
        // when
        val payload = GitHubUpdateIssueRequest(state = IssueState.CLOSED)
        val actual = patchIssue("success", 123, payload)

        // then
        assertThat(actual).isInstanceOf(Result.Success::class.java)
        val (response, _) = actual
        assertThat(response).isInstanceOf(ByteArray::class.java)
    }

    @Test
    fun `should return failure when problem with updating issue occurred`() {
        runBlocking {
            // when
            val payload = GitHubUpdateIssueRequest(state = IssueState.CLOSED)
            val actual = patchIssue("failure", 123, payload)

            // then
            assertThat(actual).isInstanceOf(Result.Failure::class.java)
            val (_, exception) = actual
            assertThat(exception).isInstanceOf(GitHubException::class.java)
        }
    }
}
