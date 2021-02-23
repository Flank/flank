package flank.corellium.client.util

import io.ktor.client.features.ServerResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend inline fun <T> withRetry(crossinline block: suspend CoroutineScope.() -> T) = coroutineScope {
    var currentDelay = 500L
    repeat(6) {
        try {
            return@coroutineScope block()
        } catch (e: ServerResponseException) {
            println("Request failed due to: ${e.message}")
            println("Waiting $currentDelay ms before $it attempt")
        }
        delay(currentDelay)
        currentDelay = (currentDelay * 2).coerceAtMost(20_000)
    }
    return@coroutineScope block()
}

suspend inline fun <T> withProgress(
    initialDelay: Long = 0,
    crossinline block: suspend CoroutineScope.() -> T
) = coroutineScope {
    if (initialDelay > 0) delay(initialDelay)
    val progress = launch {
        println("Progress")
        while (true) {
            print(".")
            delay(2500)
        }
    }
    try {
        return@coroutineScope block()
    } finally {
        progress.cancelAndJoin()
        println()
    }
}
