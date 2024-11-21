package flank.execution.parallel

import flank.execution.parallel.internal.type
import org.junit.Assert
import org.junit.Test

class ReduceKtTest {

    /**
     * Select task with dependencies, other tasks will be filtered out.
     */
    @Test
    fun `select tasks`() {
        val execute = setOf(
            A from setOf(B, C) using { },
            B from setOf(D, E) using { },
            C using { },
            D using { },
            E using { },
        )

        val expected = setOf(B, D, E)

        val actual = execute.invoke(B).map { it.type }.toSet()

        Assert.assertEquals(expected, actual)
    }

    /**
     * Selecting a type of missing task not specified in execution will throw exception.
     */
    @Test(expected = Exception::class)
    fun `select missing tasks`() {
        val execute: Tasks = setOf(C using {})

        val expected: Tasks = emptySet()

        val actual = execute.invoke(A, B).map { it.type }.toSet()

        Assert.assertEquals(expected, actual)
    }
}
