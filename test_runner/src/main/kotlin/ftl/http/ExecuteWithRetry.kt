package ftl.http

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest
import com.google.api.client.http.HttpResponseException
import ftl.config.FtlConstants
import ftl.util.PermissionDenied
import ftl.util.ProjectNotFound
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.math.exp
import kotlin.math.roundToInt

// Only use on calls that don't change state.
// Fetching status is safe to retry on timeout. Creating a matrix is not.
fun <T> AbstractGoogleJsonClientRequest<T>.executeWithRetry(): T = withRetry { this.execute() }

private inline fun <T> withRetry(crossinline block: () -> T): T = runBlocking {
    var lastError: IOException? = null
    repeat(4) {
        try {
            return@runBlocking block()
        } catch (err: IOException) {
            // We want to send every occurrence of Google API error for statistic purposes
            // https://github.com/Flank/flank/issues/701
            FtlConstants.bugsnag?.notify(FlankGoogleApiError(err))

            lastError = err
            if (err is HttpResponseException) {
                // we want to handle some FTL errors with special care
                when (err.statusCode) {
                    429 -> return@repeat
                    403 -> throw PermissionDenied(err)
                    404 -> throw ProjectNotFound(err)
                }
            }
            delay(exp(it - 1.0).roundToInt().toLong())
        }
    }
    throw IOException("Request failed", lastError)
}

private class FlankGoogleApiError(exception: Throwable) : Error(exception)
