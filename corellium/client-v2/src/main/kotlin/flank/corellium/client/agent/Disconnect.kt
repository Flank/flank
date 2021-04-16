package flank.corellium.client.agent

import io.ktor.http.cio.websocket.close

suspend fun Agent.disconnect() {
    session.close()
}
