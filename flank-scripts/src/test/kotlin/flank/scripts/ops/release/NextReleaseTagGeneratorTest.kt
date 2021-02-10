package flank.scripts.ops.release

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.BeforeClass
import org.junit.Test
import java.time.LocalDate

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

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp() {
            runCatching {
                mockkStatic(LocalDate::class) {
                    // Mockk probably has a bug because sometimes first call of
                    // every { LocalDate.now() } is failing with strange error:
                    /*
                  every/verify {} block were run several times. Recorded calls count differ between runs
Round 1: class java.time.LocalDate.of(-999999999, 1, 1), class java.time.LocalDate.of(999999999, 12, 31), class java.time.LocalDate.now()
Round 2: class java.time.LocalDate.now()
io.mockk.MockKException: every/verify {} block were run several times. Recorded calls count differ between runs
Round 1: class java.time.LocalDate.of(-999999999, 1, 1), class java.time.LocalDate.of(999999999, 12, 31), class java.time.LocalDate.now()
Round 2: class java.time.LocalDate.now()
	at io.mockk.impl.recording.SignatureMatcherDetector$detect$1.invoke(SignatureMatcherDetector.kt:25)
	at io.mockk.impl.recording.SignatureMatcherDetector.detect(SignatureMatcherDetector.kt:86)
	at io.mockk.impl.recording.states.RecordingState.signMatchers(RecordingState.kt:39)
	at io.mockk.impl.recording.states.RecordingState.round(RecordingState.kt:31)
	at io.mockk.impl.recording.CommonCallRecorder.round(CommonCallRecorder.kt:50)
	at io.mockk.impl.eval.RecordedBlockEvaluator.record(RecordedBlockEvaluator.kt:59)
	at io.mockk.impl.eval.EveryBlockEvaluator.every(EveryBlockEvaluator.kt:30)
	at io.mockk.MockKDsl.internalEvery(API.kt:92)
	at io.mockk.MockKKt.every(MockK.kt:98)
	at flank.scripts.ci.nexttag.NextReleaseTagGeneratorTest.Should start new tag for new month(NextReleaseTagGeneratorTest.kt:23)
	...
                    */
                    // simple workaround is to call `every` before test class
                    every { LocalDate.now() } returns LocalDate.of(1, 1, 1)
                }
            }
        }
    }
}
