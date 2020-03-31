package ftl.http

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClientRequest
import java.io.IOException

// Only use on calls that don't change state.
// Fetching status is safe to retry on timeout. Creating a matrix is not.
fun <T> AbstractGoogleJsonClientRequest<T>.executeWithRetry(): T {
    var lastErr: IOException? = null

    repeat(10) {
        try {
            return this.execute()
        } catch (err: IOException) {
            lastErr = err
        }
    }

    throw IOException("Request failed", lastErr)
}
