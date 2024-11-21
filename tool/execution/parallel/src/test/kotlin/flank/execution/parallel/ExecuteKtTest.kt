package flank.execution.parallel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.System.nanoTime
import java.util.concurrent.atomic.AtomicInteger

// ====================== COMMON TYPES ======================

object IntType : Parallel.Type<Int>
object A : Parallel.Type<Any>
object B : Parallel.Type<Any>
object C : Parallel.Type<Any>
object D : Parallel.Type<Any>
object E : Parallel.Type<Any>
object F : Parallel.Type<Any>

// ====================== TESTS ======================

class ExecuteKtTest {

    /**
     * Executing [Tasks] will return empty flow of [ParallelState].
     */
    @Test
    fun `run empty execution`() {
        val execute: Tasks = emptySet()
        val expected = listOf<ParallelState>()
        val actual = runBlocking { execute().toList() }
        assertEquals(expected, actual)
    }

    /**
     * Executing a [Parallel.Task] is producing initial state with accumulated value.
     */
    @Test
    fun `run single task`() {
        val execute = setOf(IntType using { 1 })
        val expected = listOf<ParallelState>(mapOf(IntType to 1))
        val actual = runBlocking { execute().toList() }
        assertEquals(expected, actual)
    }

    /**
     * Valid graph of task is run in optimized order.
     */
    @Test
    fun `run many tasks`() {
        val counter = AtomicInteger()
        val waitTime = (0..50L)
        val nextValue: ExecuteTask<Int> = {
            delay(waitTime.random())
            counter.accumulateAndGet(1) { l, r -> l + r }
        }
        val execute = setOf(
            A from setOf(B, C) using nextValue,
            B from setOf(D, E) using nextValue,
            C using nextValue,
            D using nextValue,
            E using nextValue,
        )
        val expectedOrder = mapOf(
            E to setOf(1, 2, 3),
            D to setOf(1, 2, 3),
            C to setOf(1, 2, 3, 4),
            B to setOf(3, 4),
            A to setOf(5),
        )
        val actual = runBlocking { execute().last() }

        assert(
            actual.all { (key, value) -> value in expectedOrder[key]!! }
        ) {
            expectedOrder.map { (key, expected) -> key to (actual[key] to expected) }.joinToString("\n", prefix = "\n")
        }
    }

    /**
     * Several random executions for detecting concurrent issues.
     */
    @Test
    fun `run randomized stress test`() = runBlocking {
        data class Type(val id: Int) : Parallel.Type<Any>
        repeat(100) {
            withTimeout(1000) {
                // given
                val types = (0..100).map { Type(it) }
                val used = mutableSetOf<Parallel.Type<Any>>()
                val execute: Tasks = types.map { returns ->
                    used += returns
                    val args = (types - used)
                        .shuffled()
                        .run { take((0..size).random()) }
                        .toSet()
                    returns from args using {}
                }.toSet()
                // when
                runBlocking { execute().collect() }
                // then expect no errors
            }
        }
    }

    /**
     * For failing task, the execution is accumulating [Throwable] instead of value with expected type.
     */
    @Test
    fun `run failing task`() {
        val exception = Exception()
        val execute = setOf(IntType using { throw exception })
        val expected = listOf<ParallelState>(mapOf(IntType to exception))
        val actual = runBlocking { execute().toList() }
        assertEquals(expected, actual)
    }

    /**
     * Single failing task is aborting all running and remaining tasks.
     */
    @Test
    fun `abort execution`() {
        val execute = setOf(
            A using { delay(100); throw Exception() },
            B using { delay(300) },
            C from setOf(B) using { }
        )
        val actual = runBlocking { execute().last() }
        assertTrue(actual[B].toString(), actual[B] is CancellationException)
        assertTrue(actual[C].toString(), actual[C] is Parallel.Task<*>)
    }

    /**
     * Task can access only initial and specified properties
     */
    @Test
    fun `hermetic context`() {
        class Context : Parallel.Context() {
            val initial by !D
            val b by -B
            val c by -C
        }

        val context = Parallel.Function(::Context)
        val execute = setOf(
            A from setOf(B) using context {
                initial
                b
                c // accessing this property will throw exception
            },
            B from setOf(C) using { 1 },
            C using { 2 }
        )
        val args: ParallelState = mapOf(
            D to 3
        )
        val actual = runBlocking { execute(args).last() }

        assert(actual[A] is IllegalStateException)
    }

    /**
     * Values of types that are not expected but required as dependencies,
     * can be automatically removed when there are no remaining tasks depending on them.
     */
    @Test
    fun `removing unneeded values`() {
        val execute = setOf(
            A from setOf(B) using {},
            B from setOf(E, C) using {},
            C from setOf(F, E, D) using {},
            D from setOf(E, F) using {},
            E from setOf(F) using {},
            F using {},
        )
        val expected = listOf(
            setOf(F),
            setOf(F, E),
            setOf(F, E, D),
            setOf(E, C),
            setOf(B),
            setOf(A),
        )
        val actual = runBlocking {
            execute(A)()
                .onEach { state -> println(); state.forEach(::println) }
                .map { state -> state.keys }
                .toList()
        }
        assertEquals(expected, actual)
    }

    /**
     * If option [Parallel.Sequence] is added to the initial state, the tasks should run one by on instead of parallel.
     */
    @Test
    fun `run as sequence`() {
        val timestamps = mutableListOf<Pair<Long, Long>>()

        val exec: suspend ParallelState.() -> Unit = {
            timestamps += nanoTime().also { delay(5) } to nanoTime()
        }
        val execute = setOf(
            A from setOf(B, C, D, E, F) using {},
            B using exec,
            C using exec,
            D using exec,
            E using exec,
            F using exec,
        )

        runBlocking { execute(Parallel.Sequence to Unit).collect() }

        timestamps.forEach(::println)

        timestamps.reduce { prev, next ->
            assertTrue("${prev.second} < ${next.first}", prev.second < next.first)
            next
        }
    }
}
