package flank.exection.parallel.example

import flank.exection.parallel.Parallel
import flank.exection.parallel.Parallel.Type
import flank.exection.parallel.example.ExampleExecution.A
import flank.exection.parallel.example.ExampleExecution.B
import flank.exection.parallel.example.ExampleExecution.C
import flank.exection.parallel.example.ExampleExecution.Context
import flank.exection.parallel.example.ExampleExecution.Failing
import flank.exection.parallel.example.ExampleExecution.Summary
import flank.exection.parallel.example.ExampleExecution.func
import flank.exection.parallel.execute
import flank.exection.parallel.from
import flank.exection.parallel.using
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

// ======================= Public API =======================

object ExampleExecution {

    /**
     * Context for [ExampleExecution]
     */
    interface Context : Parallel.Context {
        val args: Args
    }

    /**
     * Arguments for [ExampleExecution]
     */
    data class Args(
        val wait: Long,
        val a: Int,
        val b: Int,
        val c: Int
    )

    // Type definitions for [ExampleExecution]
    object A : Type<List<Int>>
    object B : Type<List<Int>>
    object C : Type<List<Int>>
    object Failing : Type<String>
    object Summary : Type<Map<Int, Int>>

    /**
     * Wrapper class for state values with exposed static accessors.
     */
    class Store : Parallel.Store() {
        val a by A()
        val b by B()
        val c by C()
    }

    /**
     * Factory method for creating step functions in scope of [Store].
     */
    internal val func = Parallel.Function(::Store)

    /**
     * List of tasks available for [ExampleExecution]
     */
    internal val tasks = setOf(
        errorAfterA,
        produceA,
        produceB,
        produceC,
        groupAndCount,
    )
}

fun Context.invoke() = runBlocking { execute(ExampleExecution.tasks, setOf(groupAndCount)) }

// ======================= Internal Tasks =======================

internal val errorAfterA = Failing from setOf(A) using fun Context.() = func {
    throw IllegalStateException(args.toString())
}

internal val produceA = A using fun Context.() = func {
    (0..args.a).asFlow().onEach {
        out("A" to it)
        delay((0..args.wait).random())
    }.toList()
}

internal val produceB = B using fun Context.() = func {
    (0..args.b).asFlow().onEach {
        out("B" to it)
        delay((0..args.wait).random())
    }.toList()
}

internal val produceC = C using fun Context.() = func {
    (0..args.c).asFlow().onEach {
        out("C" to it)
        delay((0..args.wait).random())
    }.toList()
}

internal val groupAndCount = Summary from setOf(A, B, C) using fun Context.() = func {
    delay(args.wait)
    (a + b + c)
        .groupBy { it }
        .mapValues { (_, values) -> values.count() }
}


