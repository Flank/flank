@file:JvmName("FetchBuildData")

package ftl.sample

import java.time.Duration
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources.createFactory

fun main() {
    val id = ""
    val password = ""
    val baseUrl = "https://enterprise-training.gradle.com"
    val url = "$baseUrl/build-export/v1/builds/since/now?stream"

    val client = OkHttpClient.Builder()
        .connectTimeout(Duration.ZERO)
        .readTimeout(Duration.ZERO)
        .authenticator(
            object : Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    return response.request
                        .newBuilder()
                        .addHeader("Authorization", Credentials.basic(id, password))
                        .build()
                }
            }
        ).build()

    val factory = createFactory(client)

    val request = Request.Builder()
        .url(url).build()
    val listener = object : EventSourceListener() {
        override fun onClosed(eventSource: EventSource) {
            println("onClosed")
            super.onClosed(eventSource)
        }

        override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
            println("onEvent $id $type $data")
            super.onEvent(eventSource, id, type, data)
        }

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
            println("onFailure $t $response")
            super.onFailure(eventSource, t, response)
        }

        override fun onOpen(eventSource: EventSource, response: Response) {
            println("Start listening to build events\n")
            println("onOpen $response")
            super.onOpen(eventSource, response)
        }
    }
    factory.newEventSource(request, listener)
}
