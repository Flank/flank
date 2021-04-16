package flank.corellium.client.console

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import kotlinx.coroutines.launch

suspend fun connectConsole(
    url: String,
    token: String
): Console =
    HttpClient {
        install(WebSockets)
    }.webSocketSession {
        url(url)
        header("Authorization", token)
    }.let { session ->
        Console(session)
    }.apply {
        session.launch {
            // drop console bash history which is received as first frame
            session.incoming.receive()

            for (frame in session.incoming) {
                lastResponseTime.set(System.currentTimeMillis())
                print(frame.data.decodeToString())
            }
        }
    }
