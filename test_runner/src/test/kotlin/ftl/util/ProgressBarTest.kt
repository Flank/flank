package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class ProgressBarTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test
    fun `progress start stop`() {
        val progress = ProgressBar()

        progress.start("hi")
        Thread.sleep(300)
        assertThat(systemOutRule.log).isEqualTo("  hi .")

        progress.stop()
        assertThat(systemOutRule.log).isEqualTo("  hi .\n")
    }
}
