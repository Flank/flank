package ftl.http

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer

class TimeoutHttpRequestInitializer(private val credential: Credential) : HttpRequestInitializer {
    override fun initialize(request: HttpRequest?) {
        credential.initialize(request)
        // timeout in milliseconds. wait 60s instead of default 20s
        request?.connectTimeout = 60 * 1000
        request?.readTimeout = 60 * 1000
    }
}
