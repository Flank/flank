package xctest

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class BashTest {

    @Test
    fun executeSmallOutput() {
        assertEquals(Bash.execute("echo ok"), "ok")
    }

    // https://github.com/TestArmada/flank/issues/200
    @Ignore
    @Test
    fun executeLargeOutput() {
        assertEquals(Bash.execute("ruby -e \"puts 'hi' * 100000\""), "hi")
    }
}
