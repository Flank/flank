package ftl.run.status

import com.google.testing.model.TestDetails
import com.google.testing.model.TestExecution
import ftl.adapter.google.toApiModel
import ftl.api.TestMatrix
import ftl.args.IArgs
import ftl.util.MatrixState
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ExecutionStatusListPrinterTest {

    @Test
    fun test() {
        // given
        val time = "time"
        val executions: List<List<TestMatrix.TestExecution>> = (0..1).map { size ->
            (0..size).map { index ->
                TestExecution().apply {
                    id = "${size}_$index"
                    state = MatrixState.PENDING
                    testDetails = TestDetails().apply {
                        errorMessage = "test error"
                        progressMessages = listOf("test progress")
                    }
                }
            }
        }.map { it.toApiModel() }
        val args = mockk<IArgs> {
            every { outputStyle } returns OutputStyle.Single
            every { flakyTestAttempts } returns 1
            every { disableSharding } returns false
        }
        val result = mutableListOf<ExecutionStatus.Change>()
        val printExecutionStatues = { changes: List<ExecutionStatus.Change> ->
            result += changes
        }

        val printList = ExecutionStatusListPrinter(
            args = args,
            printExecutionStatues = printExecutionStatues
        )
        val previous = ExecutionStatus()
        val current = ExecutionStatus(
            state = "PENDING",
            error = "test error",
            progress = listOf("test progress")
        )
        val expected = listOf(
            ExecutionStatus.Change("0 - 0", previous, current, time),
            ExecutionStatus.Change("1 - 0", previous, current, time),
            ExecutionStatus.Change("1 - 1", previous, current, time)
        )
        // when
        executions.forEach { execution ->
            printList(time, execution)
        }
        // then
        assertArrayEquals(
            expected.toTypedArray(),
            result.toTypedArray()
        )
    }
}
