package flank.exection.parallel.internal

import flank.exection.parallel.Parallel.Type
import flank.exection.parallel.from
import flank.exection.parallel.using
import org.junit.Test

class ReduceKtTest {

    private object A : Type<Unit>
    private object B : Type<Unit>
    private object C : Type<Unit>
    private object D : Type<Unit>
    private object E : Type<Unit>
    private object F : Type<Unit>

    private val execute = setOf(
        A using { },
        B using { },
        C from setOf(B) using { },
        D from setOf(A, C) using { },
        E using { },
        F from setOf(E) using { },
    )

    @Test
    fun test() {
        val omitted = setOf(E, F)
        val expected = execute.filter { it.signature.returns !in omitted }

        val actual = execute.reduce(setOf(D))

        assert(expected.containsAll(actual))
        assert(expected.size == actual.size)
    }

    @Test
    fun test2() {

        val actual = execute.filter { it.signature.returns != C }.toSet().reduce(setOf(D))

        actual.forEach(::println)
    }
}
