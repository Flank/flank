package ftl.util

import com.google.common.truth.Truth.assertThat
import ftl.config.FtlConstants.isMacOS
import ftl.run.exception.FlankGeneralError
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(FlankTestRunner::class)
class BashTest {

    @Test
    fun executeStderr() {
        assertThat(Bash.execute("echo a 1>&2")).isEqualTo("a")
    }

    @Test(expected = FlankGeneralError::class)
    fun executeStderrExitCode1() {
        assertThat(Bash.execute("echo not an error 1>&2; exit 1")).isEmpty()
    }

    @Test
    fun executeNoOutput() {
        assertThat(Bash.execute(" ")).isEmpty()
    }

    @Test
    fun executeSmallOutput() {
        assertThat(Bash.execute("echo ok")).isEqualTo("ok")
    }

    @Test
    fun executeLargeOutput() {
        // gohello is a binary that outputs 100k 'hi' to stdout
        val os = if (isMacOS) {
            "mac"
        } else {
            "linux"
        }
        val cmd = "./src/test/kotlin/ftl/fixtures/tmp/gohello/bin/$os/gohello"
        // ensure binary is marked executable. unzip may have removed the executable bit.
        File(cmd).setExecutable(true)
        assertThat(Bash.execute(cmd).length).isEqualTo(200_000)
    }
}
