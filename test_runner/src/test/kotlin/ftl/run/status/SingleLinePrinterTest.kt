package ftl.run.status

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class SingleLinePrinterTest {
    @Rule
    @JvmField
    val systemOutRule = SystemOutRule().enableLog()!!

    @Test
    fun test() {
        // given
        val time = "time"
        val printChanges = SingleLinePrinter()
        val changes1 = listOf(
            ExecutionStatus.Change(
                name = "name1",
                previous = ExecutionStatus(),
                current = ExecutionStatus(state = "state0"),
                time = time
            ),
            ExecutionStatus.Change(
                name = "name2",
                previous = ExecutionStatus(),
                current = ExecutionStatus(state = "state0"),
                time = time
            )
        )
        val changes2 = changes1.mapIndexed { index, it ->
            it.copy(
                previous = it.current,
                current = it.current.copy(state = "state${index % 2}")
            )
        }

        // when
        printChanges(changes1)
        printChanges(changes2)

        // then
        assertEquals(
            listOf(
                "\r ",
                "\r  time Test executions status: state0:2",
                "\r                                        ",
                "\r  time Test executions status: state0:1, state1:1"
            ).joinToString(""),
            systemOutRule.log
        )
    }
}
