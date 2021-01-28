package flank.scripts.ops.pullrequest

import com.google.common.truth.Truth.assertThat
import flank.scripts.data.github.objects.GithubPullRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class FindReferenceIssueTest {

    @Test
    fun `Should find reference number in body`() {
        // given
        val expectedNumber = 111
        val mockedPullRequest = mockk<GithubPullRequest>() {
            every { body } returns "Fixes #$expectedNumber\nTest description"
        }

        // when
        val actual = mockedPullRequest.findReferenceNumber()

        // then
        assertThat(actual).isEqualTo(expectedNumber)
    }

    @Test
    fun `Should find reference number in branch, if not found in body`() {
        // given
        val expectedNumber = 111
        val mockedPullRequest = mockk<GithubPullRequest>() {
            every { body } returns "Test description"
            every { head } returns mockk {
                every { ref } returns "#${expectedNumber}_test_task"
            }
        }

        // when
        val actual = mockedPullRequest.findReferenceNumber()

        // then
        assertThat(actual).isEqualTo(expectedNumber)
    }

    @Test
    fun `Should return null if reference number not found in body and branch`() {
        // given
        val mockedPullRequest = mockk<GithubPullRequest>() {
            every { body } returns "Test description"
            every { head } returns mockk {
                every { ref } returns "#test_task"
            }
        }

        // when
        val actual = mockedPullRequest.findReferenceNumber()

        // then
        assertThat(actual).isNull()
    }
}
