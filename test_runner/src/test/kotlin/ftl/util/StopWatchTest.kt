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
}
