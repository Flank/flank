package flank.scripts.ci.nexttag

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import java.time.LocalDate
import org.junit.Test

class NextReleaseTagGeneratorTest {

    @Test
    fun `Should increment value for next tag`() {
        mockkStatic(LocalDate::class) {
            every { LocalDate.now() } returns LocalDate.of(2020, 8, 1)
            assertThat(generateNextReleaseTag("v20.08.1")).isEqualTo("v20.08.2")
        }
    }

    @Test
    fun `Should start new tag for new month`() {
        mockkStatic(LocalDate::class) {
            every { LocalDate.now() } returns LocalDate.of(2020, 9, 1)
            assertThat(generateNextReleaseTag("v20.08.1")).isEqualTo("v20.09.0")
        }
    }
}
