package ftl.run.status

import ftl.doctor.assertEqualsIgnoreNewlineStyle
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class VerbosePrinterTest {
    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun test() {
        // given
        val printChanges = VerbosePrinter

        // when
        printChanges(PrinterTestUtil.changes1)
        printChanges(PrinterTestUtil.changes2)

        // then
        assertEqualsIgnoreNewlineStyle(
            listOf(
                "  time name1 state0",
                "  time name2 state0",
                "  time name2 state1"
            ).joinToString("\n", postfix = "\n"),
            systemOutRule.log
        )
    }
}
