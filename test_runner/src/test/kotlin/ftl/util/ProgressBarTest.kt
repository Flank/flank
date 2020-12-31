package ftl.util

import com.google.common.truth.Truth.assertThat
import flank.common.OutputLogLevel
import flank.common.normalizeLineEnding
import flank.common.setLogLevel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class ProgressBarTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Before
    fun beforeTest() {
        setLogLevel(OutputLogLevel.DETAILED)
    }

    @Test
    fun `progress start stop`() {
        val progress = ProgressBar()

        progress.start("hi")
        Thread.sleep(300)
        assertThat(systemOutRule.log.normalizeLineEnding()).isEqualTo("  hi.")

        progress.stop()
        assertThat(systemOutRule.log.normalizeLineEnding()).isEqualTo("  hi.\n")
    }

    @Test
    fun `should properly wrap progress bar execution using helper function for success`() {
        // given
        val action: Runnable = mockk {
            every { run() } returns Unit
        }
        val onError: Runnable = mockk {
            every { run() } returns Unit
        }

        // when
        runWithProgress(
            startMessage = "TEST",
            action = { action.run() },
            onError = { onError.run() }
        )

        // then
        assertThat(systemOutRule.log).contains("  TEST")
        verify { action.run() }
        verify(exactly = 0) { onError.run() }
    }

    @Test
    fun `should properly wrap progress bar execution using helper function for failure`() {
        // given
        val action: Runnable = mockk {
            every { run() } returns Unit
        }
        val onError: Runnable = mockk {
            every { run() } returns Unit
        }

        // when
        runWithProgress(
            startMessage = "TEST",
            action = {
                action.run()
                throw Exception()
            },
            onError = { onError.run() }
        )

        // then
        assertThat(systemOutRule.log).contains("  TEST")
        verify { action.run() }
        verify { onError.run() }
    }
}
