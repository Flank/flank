package ftl.http

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest
import com.google.api.client.http.HttpResponseException
import ftl.config.FtlConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.IOException
import kotlin.math.exp
import kotlin.math.roundToInt

// Only use on calls that don't change state.
// Fetching status is safe to retry on timeout. Creating a matrix is not.
fun <T> AbstractGoogleJsonClientRequest<T>.executeWithRetry(): T = withRetry { this.execute() }

private inline fun <T> withRetry(crossinline block: () -> T): T = runBlocking {
    var lastErr: IOException? = null

    repeat(4) {
        try {
            return@runBlocking block()
        } catch (err: IOException) {
            // We want to send every occurrence of Google API error for statistic purposes
            // https://github.com/Flank/flank/issues/701
            FtlConstants.bugsnag?.notify(FlankGoogleApiError(err))

            lastErr = err
            // HttpStatusCodes from google api client does not have 429 code
            if (err is HttpResponseException && err.statusCode == 429) {
                return@repeat
            }
            delay(exp(it - 1.0).roundToInt().toLong())
        }
    }

    throw IOException("Request failed", lastErr)
}

private class FlankGoogleApiError(exception: Throwable) : Error(exception)
