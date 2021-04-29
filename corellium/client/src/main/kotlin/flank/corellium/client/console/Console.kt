package flank.corellium.client.console

import io.ktor.client.features.websocket.ClientWebSocketSession
import kotlinx.coroutines.CoroutineScope

class Console internal constructor(
    internal val session: ClientWebSocketSession
) : CoroutineScope by session {
    internal var lastResponseTime = System.currentTimeMillis()
}
