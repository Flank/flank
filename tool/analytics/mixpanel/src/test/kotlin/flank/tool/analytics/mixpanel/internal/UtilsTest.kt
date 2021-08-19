package flank.tool.analytics.mixpanel.internal

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class UtilsTest {

    @Test
    fun `should not retry succeed code`() {
        // given
        val mockedRun = mockk<Runnable>()
        every { mockedRun.run() } returns Unit

        // when
        retryWithSilentFailure(5) {
            mockedRun.run()
        }

        // then
        verify(exactly = 1) { mockedRun.run() }
    }

    @Test
    fun `should retry given times`() {
        // given
        val mockedRun = mockk<Runnable>()
        every { mockedRun.run() } throws IllegalStateException("test")

        // when
        retryWithSilentFailure(5) {
            mockedRun.run()
        }

        // then
        verify(exactly = 5 + 1) { mockedRun.run() }
    }
}
