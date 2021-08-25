package ftl.http

import com.google.api.client.http.HttpRequest
import com.google.auth.Credentials
import com.google.auth.http.HttpCredentialsAdapter

/** Resolves timeout flakiness by increasing the timeout to 60s (the default is 20s) **/
class HttpTimeoutIncrease(credentials: Credentials) : HttpCredentialsAdapter(credentials) {
    override fun initialize(request: HttpRequest?) {
        super.initialize(request)

        request?.connectTimeout = 60 * 1000
        request?.readTimeout = 60 * 1000
    }
}
