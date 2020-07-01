package ftl.http

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.HttpStatusCodes
import ftl.config.FtlConstants
import ftl.util.PermissionDenied
import ftl.util.ProjectNotFound
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.math.exp
import kotlin.math.roundToLong

// Only use on calls that don't change state.
// Fetching status is safe to retry on timeout. Creating a matrix is not.
fun <T> AbstractGoogleJsonClientRequest<T>.executeWithRetry(): T = withRetry { this.execute() }

private inline fun <T> withRetry(crossinline block: () -> T): T = runBlocking {
    var lastError: IOException? = null
    repeat(MAX_TRIES) { repeatCounter ->
        val executionResult = tryExecuteBlock(block)
        if (executionResult.isSuccess()) return@runBlocking executionResult.success()

        val error = executionResult.error()
        // We want to send every occurrence of Google API error for statistic purposes
        // https://github.com/Flank/flank/issues/701
        FtlConstants.bugsnag?.notify(FlankGoogleApiError(error))
        lastError = error
        if (handleFtlError(error)) return@repeat
        delay(calculateDelayTime(repeatCounter))
    }
    throw IOException("Request failed", lastError)
}
private const val MAX_TRIES = 4

private inline fun <T> tryExecuteBlock(block: () -> T): ExecutionBlockResult<T> = try {
    ExecutionBlockResult(block(), null)
} catch (ioError: IOException) {
    ExecutionBlockResult(null, ioError)
}

private data class ExecutionBlockResult<T>(private val success: T?, private val error: IOException?) {
    fun isSuccess() = success != null
    fun success() = success!!
    fun error() = error!!
}

private fun handleFtlError(error: IOException) = (error as? HttpResponseException)?.let {
    // we want to handle some FTL errors with special care
    when (it.statusCode) {
        HttpStatusCodes.STATUS_CODE_FORBIDDEN -> throw PermissionDenied(it)
        HttpStatusCodes.STATUS_CODE_NOT_FOUND -> throw ProjectNotFound(it)
        429 -> true
        else -> false
    }
} ?: false

private fun calculateDelayTime(repeatCounter: Int) = exp(repeatCounter - 1.0).roundToLong()

private class FlankGoogleApiError(exception: Throwable) : Error(exception)
