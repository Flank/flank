package flank.scripts.ci.nexttag

import com.github.kittinunf.result.Result
import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import flank.scripts.ci.releasenotes.GitHubRelease
import flank.scripts.github.getLatestReleaseTag
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.Year

@RunWith(FuelTestRunner::class)
class NextReleaseTagCommandTest {

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Rule
    @JvmField
    val systemExit = ExpectedSystemExit.none()!!

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Should return properly message when success`() {
        mockkStatic(
            "flank.scripts.github.GithubApiKt",
            "java.time.LocalDate",
            "java.time.Year"
        ) {
            every { LocalDate.now() } returns LocalDate.of(2020, 9, 16)
            every { Year.now() } returns Year.of(2020)
            coEvery { getLatestReleaseTag(any()) } returns Result.success(GitHubRelease("v20.09.0"))

            // when
            NextReleaseTagCommand().main(arrayOf("--token=success"))

            // expected
            assertThat(systemOutRule.log).contains("v20.09.1")
        }
    }

    @Test
    fun `Should return with exit code 1 when failure`() {
        // expect
        systemExit.expectSystemExitWithStatus(1)
        systemExit.checkAssertionAfterwards {
            assertThat(systemOutRule.log).contains("Error while doing GitHub request")
        }
        // when
        NextReleaseTagCommand().main(arrayOf("--token=failure"))
    }
}
