package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class BashTest {

    @Test
    fun executeStderr() {
        assertThat(Bash.execute("echo a 1>&2")).isEmpty()
    }

    @Test(expected = RuntimeException::class)
    fun executeStderrExitCode1() {
        assertThat(Bash.execute("echo not an error 1>&2; exit 1")).isEmpty()
    }

    @Test
    fun executeNoOutput() {
        assertThat(Bash.execute("")).isEmpty()
    }

    @Test
    fun executeSmallOutput() {
        assertThat(Bash.execute("echo ok")).isEqualTo("ok")
    }

    @Test
    fun executeLargeOutput() {
        assertThat(Bash.execute("ruby -e \"puts 'hi' * 100_000\"").length).isEqualTo(200_000)
    }
}
