package flank.exection.parallel

import flank.exection.parallel.Example.Args
import flank.exection.parallel.Example.Hello
import flank.exection.parallel.Example.Summary
import flank.exection.parallel.Parallel.Type
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

// ======================= Public API =======================

private object Example {

    /**
     * Context for [Example].
     * Wrapper for state values with exposed static accessors.
     */
    class Context : Parallel.Context() {
        val args by Args()
        val a by A()
        val b by B()
        val c by C()
        val hello by Hello()
    }

    /**
     * Arguments for [Example].
     */
    data class Args(
        val wait: Long,
        val a: Int,
        val b: Int,
        val c: Int
    ) {
        companion object : Type<Args>
    }

    object A : Type<List<Int>>
    object B : Type<List<Int>>
    object C : Type<List<Int>>
    object Summary : Type<Map<Int, Int>>
    object Hello : Type<String> {
        object Failing : Type<Unit>
    }

    /**
     * Factory method for creating step functions in scope of [Context].
     */
    val func = Parallel.Task.Body(::Context)

    /**
     * List of tasks available for [Example]
     */
    val execute by lazy {
        setOf(
            hello,
            helloFail,
            produceA,
            produceB,
            produceC,
            groupAndCount,
        )
    }

    // ======================= Internal Tasks =======================

    private val hello = Hello using {
        delay(100)
        "hello"
    }

    private val helloFail = Hello.Failing from setOf(Hello) using func {
        throw IllegalStateException(hello)
    }

    private val produceA = A using func {
        (0..args.a).asFlow().onEach {
            out("A" to it)
            delay((0..args.wait).random())
        }.toList()
    }

    private val produceB = B using func {
        (0..args.b).asFlow().onEach {
            out("B" to it)
            delay((0..args.wait).random())
        }.toList()
    }

    private val produceC = C using func {
        (0..args.c).asFlow().onEach {
            out("C" to it)
            delay((0..args.wait).random())
        }.toList()
    }

    private val groupAndCount = Summary from setOf(A, B, C) using func {
        delay(args.wait)
        (a + b + c)
            .groupBy { it }
            .mapValues { (_, values) -> values.count() }
    }
}

// ======================= Example Run =======================

fun main() {
    runBlocking {
        Example.execute(
            Summary,
            Hello,
            Hello.Failing,
        )(
            Args to Args(
                wait = 50,
                a = 5,
                b = 8,
                c = 13,
            ),
            Parallel.Logger to { log: Any ->
                println(log)
            }
        ).last().let { state ->
            println()
            state.forEach(::println)
            println()
        }
    }
}
