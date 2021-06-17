package flank.corellium.client.util

import io.ktor.client.features.ServerResponseException
import kotlinx.coroutines.delay
import kotlin.math.pow

suspend inline fun <R> withRetry(crossinline block: suspend () -> R): R =
    (0 until 5).mapNotNull { multi ->
        try {
            block()
        } catch (e: ServerResponseException) {
            val wait = (2.0).pow(multi).times(1000).toLong()
            delay(wait)
            null
        }
    }.firstOrNull() ?: block()
