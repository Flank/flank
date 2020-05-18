package ftl.run.status

object PrinterTestUtil {
    const val time = "time"
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
}

fun String.filterMockk() = filterLines { !contains("io.mockk") }
private fun String.filterLines(f: String.() -> Boolean) = lineSequence().filter(f).joinToString("\n")
