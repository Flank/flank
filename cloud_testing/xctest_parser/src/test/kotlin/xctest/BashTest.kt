package xctest

import org.junit.Assert.assertEquals
import org.junit.Test

class BashTest {

    @Test
    fun executeSmallOutput() {
        assertEquals(Bash.execute("echo ok"), "ok")
    }

    @Test
    fun executeLargeOutput() {
        assertEquals(Bash.execute("ruby -e \"puts 'hi' * 100_000\"").length, 200_000)
    }
}
