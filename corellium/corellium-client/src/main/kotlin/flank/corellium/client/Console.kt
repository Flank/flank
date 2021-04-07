package flank.corellium.client

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class Console(
    url: String,
    token: String
) {
    private var open = true
    private val socket = runBlocking {
        HttpClient {
            install(WebSockets)
        }.webSocketSession {
            url(url)
            header("Authorization", token)
        }.apply {
            launch {
                var closingJob: Job? = null
                for (frame in incoming) {
                    if (frame is Frame.Binary) {
                        closingJob?.let {
                            it.cancel()

                            // we want to skip first message since corellium sends old console logs
                            print(frame.data.decodeToString())
                        }
                        closingJob = launch {
                            delay(5_000)
                            open = false
                        }
                    }
                }
            }
        }
    }

    suspend fun sendCommand(command: String) =
        socket.send(Frame.Binary(true, (command + "\n").encodeToByteArray()))

    suspend fun waitUntilFinished() {
        while (open) delay(5_000)
    }
}
