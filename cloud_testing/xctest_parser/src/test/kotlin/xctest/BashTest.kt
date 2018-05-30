package xctest

import org.junit.Assert.assertEquals
import org.junit.Test

class BashTest {

    @Test
    fun executeStderr() {
        assertEquals("", Bash.execute("echo a 1>&2"))
    }

    @Test(expected = RuntimeException::class)
    fun executeStderrExitCode1() {
        assertEquals("", Bash.execute("echo not an error 1>&2; exit 1"))
    }

    @Test
    fun executeNoOutput() {
        assertEquals("", Bash.execute(""))
    }

    @Test
    fun executeSmallOutput() {
        assertEquals("ok", Bash.execute("echo ok"))
    }

    @Test
    fun executeLargeOutput() {
        assertEquals(200_000, Bash.execute("ruby -e \"puts 'hi' * 100_000\"").length)
    }
}
