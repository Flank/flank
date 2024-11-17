package flank.execution.parallel.benchmark

import flank.execution.parallel.Parallel
import flank.execution.parallel.Tasks
import flank.execution.parallel.from
import flank.execution.parallel.internal.args
import flank.execution.parallel.using
import flank.execution.parallel.validate
import kotlin.system.measureTimeMillis

fun main() {
    data class Type(val id: Int) : Parallel.Type<Any>

    val types = (0..1000).map { Type(it) }

    val used = mutableSetOf<Parallel.Type<Any>>()

    val execute: Tasks = types.map { returns ->
        used += returns
        val args = (types - used)
            .shuffled()
            .run { take((0..size / 10).random()) }
            .toSet()
        returns from args using {}
    }.toSet()

    val edges = execute.sumOf { task -> task.args.size }

    println("edges: $edges")
    println()

    repeat(10) {
        measureTimeMillis { execute.validate() }.let(::println)
    }
}
