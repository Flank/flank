package flank.scripts.cli.release

import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class DeleteOldTagCommandTest {

    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun `Should return properly message when success`() {
        // when
        DeleteOldTagCommand().main(arrayOf("--git-tag=success", "--username=1", "--token=1"))

        // then
        assertThat(systemOutRule.log).contains("Tag success was deleted")
    }

    @Test
    fun `Should return with exit code 1 when failure`() {
        // when
        DeleteOldTagCommand().main(arrayOf("--git-tag=failure", "--username=1", "--token=1"))

        // then
        assertThat(systemOutRule.log).contains("Error while doing GitHub request")
    }
}
