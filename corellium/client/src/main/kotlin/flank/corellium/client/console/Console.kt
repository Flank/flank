package flank.corellium.client.console

import io.ktor.client.features.websocket.ClientWebSocketSession
import kotlinx.coroutines.CoroutineScope

/**
 * The context of serial console connection. Allows to write and read the serial console of the virtual device.
 *
 * @property session Web socket session with bidirectional stream to the serial console.
 */
class Console internal constructor(
    internal val session: ClientWebSocketSession
) : CoroutineScope by session {
    internal var lastResponseTime = System.currentTimeMillis()
}
