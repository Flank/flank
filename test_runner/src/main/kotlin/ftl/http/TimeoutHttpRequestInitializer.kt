package ftl.http

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer

class TimeoutHttpRequestInitializer(private val googleCredential: GoogleCredential) : HttpRequestInitializer {
    override fun initialize(request: HttpRequest?) {
        googleCredential.initialize(request)
        // timeout in milliseconds. wait 60s instead of default 20s
        request?.connectTimeout = 60 * 1000
        request?.readTimeout = 60 * 1000
    }
}
