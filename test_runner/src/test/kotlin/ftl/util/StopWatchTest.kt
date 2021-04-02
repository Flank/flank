package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.run.exception.FlankGeneralError
import org.junit.Test

class StopWatchTest {

    @Test(expected = FlankGeneralError::class)
    fun `stopWatch errorOnCheckWithoutStart`() {
        StopWatch().check()
    }

    @Test
    fun `stopWatch recordTime`() {
        val watch = StopWatch().start()
        assertThat(watch.check().formatted(alignSeconds = true)).isNotEmpty()
        assertThat(watch.check().formatted()).isNotEmpty()
    }

    @Test
    fun `should properly format time with align spaces set to true`() {
        // given
        val expectedResults = arrayOf(
            "1m  1s",
            "1m 11s"
        )
        val testDurations = arrayOf(
            Duration(61),
            Duration(71)
        )

        // when
        val actual = testDurations.map { it.formatted(true) }

        // then
        actual.forEachIndexed { index, result ->
            assertThat(result).isEqualTo(expectedResults[index])
        }
    }

    @Test
    fun `should properly format time with align spaces set to false`() {
        // given
        val expectedResults = arrayOf(
            "1m 1s",
            "1m 11s"
        )
        val testDurations = arrayOf(
            Duration(61),
            Duration(71)
        )

        // when
        val actual = testDurations.map { it.formatted() }

        // then
        actual.forEachIndexed { index, result ->
            assertThat(result).isEqualTo(expectedResults[index])
        }
    }
}
