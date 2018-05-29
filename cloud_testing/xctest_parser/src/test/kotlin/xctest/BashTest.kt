package xctest

import org.junit.Assert.assertEquals
import org.junit.Test

class BashTest {

    @Test
    fun executeSmallOutput() {
        assertEquals("ok", Bash.execute("echo ok"))
    }

    @Test
    fun executeLargeOutput() {
        assertEquals(200_000, Bash.execute("ruby -e \"puts 'hi' * 100_000\"").length)
    }
}
