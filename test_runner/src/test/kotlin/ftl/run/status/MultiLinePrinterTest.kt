package ftl.run.status

import ftl.run.status.PrinterTestUtil.changes1
import ftl.run.status.PrinterTestUtil.changes2
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.fusesource.jansi.Ansi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class MultiLinePrinterTest {
    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun test() {
        // given
        val ansi = spyk(Ansi.ansi()) { every { this@spyk.toString() } returns "" }
        val printChanges = MultiLinePrinter { ansi }

        // when
        printChanges(changes1)
        printChanges(changes2)

        // then
        assertEquals(
            listOf(
                "  time name1 state0",
                "  time name2 state0",
                "  time name1 state0",
                "  time name2 state1"
            ).joinToString("\n", postfix = "\n"),
            systemOutRule.log.filterMockk()
        )
        verify(exactly = 2) {
            ansi.cursorUpLine()
            ansi.eraseLine()
        }
    }
}
