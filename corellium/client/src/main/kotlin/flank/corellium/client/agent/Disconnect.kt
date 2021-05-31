package flank.corellium.client.agent

import io.ktor.http.cio.websocket.close

/**
 * Disconnect agent connection.
 */
suspend fun Agent.disconnect() {
    session.close()
}
