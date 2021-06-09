package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.from
import flank.exection.parallel.using
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidateKtTest {

    private object A : Parallel.Type<Unit>
    private object B : Parallel.Type<Unit>
    private object C : Parallel.Type<Unit>
    private object D : Parallel.Type<Unit>

    private val execute = setOf(
        A using { },
        B from setOf(A) using { },
        C from setOf(B) using { },
        D from setOf(A, C) using { },
    )

    /**
     * Return empty map when there are no missing dependencies.
     */
    @Test
    fun test() {
        execute.validate()
    }

    /**
     * Return missing map of dependencies dependencies
     */
    @Test
    fun test2() {
        val expected = mapOf(
            B to setOf(A),
            D to setOf(A)
        )
        try {
            execute.drop(1).toSet().validate()
        } catch (e: Parallel.ValidationError) {
            assertEquals(expected, e.data)
        }
    }
}
