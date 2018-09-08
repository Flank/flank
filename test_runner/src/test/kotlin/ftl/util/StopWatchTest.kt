package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StopWatchTest {

    @Test(expected = RuntimeException::class)
    fun stopWatch_errorOnCheckWithoutStart() {
        StopWatch().check()
    }

    @Test
    fun stopWatch_recordTime() {
        val watch = StopWatch().start()
        assertThat(watch.check(indent = true)).isNotEmpty()
        assertThat(watch.check()).isNotEmpty()
    }
}
