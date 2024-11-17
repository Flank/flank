package flank.execution.parallel

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import java.lang.NullPointerException

class ValidateKtTest {

    /**
     * Validating proper execution will just return it.
     */
    @Test
    fun `validate proper execution`() {
        val expected = setOf(
            A from setOf(B) using { },
            B from setOf(C, D) using { },
            C from setOf(D) using { },
            D using { }
        )
        assertEquals(
            expected,
            expected.validate(),
        )
    }

    /**
     * If the validator function is specified in tasks it will validate arguments.
     * The failing validation will throw [NullPointerException]
     */
    @Test(expected = NullPointerException::class)
    fun `validate missing argument`() {
        class Context : Parallel.Context() {
            val initial by !IntType
        }

        val execute = setOf(validator(::Context))
        execute.validate()
    }

    /**
     * If the validator function is specified in tasks it will validate arguments.
     * The failing validation will throw [ClassCastException]
     */
    @Test(expected = ClassCastException::class)
    fun `validate argument type`() {
        class Context : Parallel.Context() {
            val initial by !IntType
        }

        val args: ParallelState = mapOf(IntType to "asd")
        val execute = setOf(validator(::Context))
        execute.validate(args)
    }

    /**
     * Running graph with unresolved dependencies should throw [Parallel.DependenciesError.Missing].
     */
    @Test
    fun `missing dependencies`() {
        val execute = setOf(
            A from setOf(B, C) using { },
            D from setOf(E, C) using { },
        )
        val expected = mapOf(
            A to setOf(B, C),
            D to setOf(E, C),
        )
        try {
            execute.validate()
            fail()
        } catch (error: Parallel.DependenciesError.Missing) {
            assertEquals(expected, error.data)
        }
    }

    /**
     * Detect duplicated return values of tasks.
     */
    @Test
    fun `duplicate dependencies 1`() {
        val execute = setOf(
            A from setOf(B) using { },
            A from setOf(C) using { },
            A from setOf(D) using { },
            B from setOf(D) using { },
            B from setOf(C) using { },
            D using {},
            C using {}
        )
        val expected = mapOf(
            A to 2,
            B to 1,
        )
        try {
            execute.validate()
            fail()
        } catch (error: Parallel.DependenciesError.Duplicate) {
            assertEquals(expected, error.data)
        }
    }

    /**
     * Detect duplicated return values of state tasks.
     */
    @Test
    fun `duplicate dependencies 2`() {
        val args: ParallelState = mapOf(
            A to Unit,
        )
        val execute = setOf(
            A from setOf(B) using { },
            A from setOf(C) using { },
            B using {},
            C using {},
        )
        val expected = mapOf(
            A to 2,
        )
        try {
            execute.validate(args)
            fail()
        } catch (error: Parallel.DependenciesError.Duplicate) {
            assertEquals(expected, error.data)
        }
    }

    @Test
    fun `cyclic dependencies 1`() {
        val execute = setOf(
            A from setOf(A) using { },
        )
        val expected = listOf(listOf(A))
        try {
            execute.validate()
            fail()
        } catch (e: Parallel.DependenciesError.Cycles) {
            assertEquals(expected, e.data)
        }
    }

    @Test
    fun `cyclic dependencies 2`() {
        val execute = setOf(
            A from setOf(B) using { },
            B from setOf(A) using { },
        )
        val expected = listOf(listOf(A, B, A))
        try {
            execute.validate()
            fail()
        } catch (e: Parallel.DependenciesError.Cycles) {
            assertEquals(expected, e.data)
        }
    }

    @Test
    fun `cyclic dependencies 3`() {
        val execute = setOf(
            A from setOf(B) using { },
            B from setOf(C) using { },
            C from setOf(A) using { },
        )
        val expected = listOf(listOf(A, B, C, A))
        try {
            execute.validate()
            fail()
        } catch (e: Parallel.DependenciesError.Cycles) {
            assertEquals(expected, e.data)
        }
    }
}
