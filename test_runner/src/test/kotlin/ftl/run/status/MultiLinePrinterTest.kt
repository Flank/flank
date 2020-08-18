package ftl.run.status

import ftl.run.status.PrinterTestUtil.changes1
import ftl.run.status.PrinterTestUtil.changes2
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
        val printChanges = MultiLinePrinter()

        // when
        printChanges(changes1)
        printChanges(changes2)

        // then
        assertEquals(
            listOf(
                "  time name1 state0",
                "  time name2 state0",
                "  time name2 state1"
            ).joinToString("\n", postfix = "\n"),
            systemOutRule.log.filterMockk()
        )
    }
}
