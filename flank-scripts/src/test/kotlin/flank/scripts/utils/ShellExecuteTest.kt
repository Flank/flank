package flank.scripts.utils

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ShellExecuteTest {

    @Test
    fun `Should run process only once when success`() {
        // given
        val processBuilder = mockk<ProcessBuilder>() {
            every { start().waitFor() } returns 0
        }

        // when
        val actual = processBuilder.startWithRetry(0)

        // then
        verify(exactly = 1) { processBuilder.start() }
        assertThat(actual).isEqualTo(0)
    }

    @Test
    fun `Should retry process given number of retries when failed`() {
        // given
        val processBuilder = mockk<ProcessBuilder>() {
            every { start().waitFor() } returns 1
        }

        // when
        val actual = processBuilder.startWithRetry(3)

        // then
        verify(exactly = 3) { processBuilder.start() }
        assertThat(actual).isEqualTo(1)
    }

    @Test
    fun `Should not retry process when exception occurs`() {
        // given
        val processBuilder = mockk<ProcessBuilder>() {
            every { start().waitFor() } throws Exception("test")
        }

        // when
        val actual = processBuilder.startWithRetry(3)

        // then
        verify(exactly = 1) { processBuilder.start() }
        assertThat(actual).isEqualTo(-1)
    }
}
