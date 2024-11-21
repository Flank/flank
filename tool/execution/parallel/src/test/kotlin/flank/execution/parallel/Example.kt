package flank.execution.parallel

import flank.execution.parallel.Example.Args
import flank.execution.parallel.Example.Hello
import flank.execution.parallel.Example.Summary
import flank.execution.parallel.Parallel.Type
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

// ======================= Public API =======================

private object Example {
    /**
     * Arguments for [Example].
     */
    data class Args(
        val wait: Long,
        val a: Int,
        val b: Int,
        val c: Int
    ) {
        /**
         * Specify type for [Args] so can be added to [ParallelState]
         */
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
     * Context for [Example] scope.
     * Wrapper for state values with exposed static accessors.
     */
    class Context : Parallel.Context() {
        // Initial part of state.
        // Validated before execution
        val args by !Args

        // Values evaluated during execution by tasks.
        val a by -A
        val b by -B
        val c by -C
        val hello by -Hello
    }

    /**
     * Factory method for creating task functions with [Context].
     */
    val context = Parallel.Function(::Context)

    /**
     * List of tasks in [Example] scope
     */
    val execute: Tasks by lazy {
        setOf(
            validate,
            hello,
            helloFail,
            produceA,
            produceB,
            produceC,
            groupAndCount,
        )
    }

    // ======================= Tasks =======================

    private val validate: Parallel.Task<Unit> = validator(::Context)

    private val hello: Parallel.Task<String> = Hello using {
        delay(100)
        "hello"
    }

    private val helloFail = Hello.Failing from setOf(Hello) using context {
        throw IllegalStateException(hello)
    }

    private val produceA = A using context {
        (0..args.a).asFlow().onEach {
            out("A" to it)
            delay((0..args.wait).random())
        }.toList()
    }

    private val produceB = B using context {
        (0..args.b).asFlow().onEach {
            out("B" to it)
            delay((0..args.wait).random())
        }.toList()
    }

    private val produceC = C using context {
        (0..args.c).asFlow().onEach {
            out("C" to it)
            delay((0..args.wait).random())
        }.toList()
    }

    private val groupAndCount = Summary from setOf(A, B, C) using context {
        delay(args.wait)
        (a + b + c)
            .groupBy { it }
            .mapValues { (_, values) -> values.count() }
    }
}

// ======================= Example Run =======================

fun main() {
    val args: ParallelState = mapOf(
        // Specify initial state.
        // Commend initial Args to simulate failure of initial validation.
        Args to Args(
            wait = 50,
            a = 5,
            b = 8,
            c = 13,
        ),
        // Parallel.Logger to { log: Any -> println(log) },
        // Parallel.Sequence to Unit,
    )

    Example.run {
        execute + context.validate
    }.validate(
        // Comment line below to simulate error on context.validator
        args
    )

    runBlocking {
        Example.execute(
            // Expect selected tasks.
            Summary,
            Hello,
            // Uncomment to simulate failing execution.
            // Hello.Failing,
        )(args).collect { state ->
            // Print final state
            println()
            state.forEach(::println)
            println()
        }
    }
}
