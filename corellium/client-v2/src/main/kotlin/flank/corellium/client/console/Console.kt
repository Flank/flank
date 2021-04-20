package flank.corellium.client.console

import io.ktor.client.features.websocket.ClientWebSocketSession
import java.util.concurrent.atomic.AtomicLong

class Console internal constructor(
    internal val session: ClientWebSocketSession
) {
    internal val lastResponseTime: AtomicLong = AtomicLong(System.currentTimeMillis())
}
