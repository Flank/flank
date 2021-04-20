package flank.corellium.client.console

import io.ktor.client.features.websocket.ClientWebSocketSession
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope

class Console internal constructor(
    internal val session: ClientWebSocketSession
) : CoroutineScope by session {
    internal val lastResponseTime: AtomicLong = AtomicLong(System.currentTimeMillis())
}
