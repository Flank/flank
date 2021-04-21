package flank.corellium.logs

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    Unit.javaClass.classLoader
        .getResourceAsStream("example_android_logs.log")
        .bufferedReader()
        .lineSequence()
        .asFlow()
        .groupLines()
        .parseChunks()
        .collect {
            println()
            println(it.type + it.code)
            it.map.forEach(::println)
        }
}
