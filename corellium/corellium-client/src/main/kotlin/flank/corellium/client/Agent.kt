package flank.corellium.client

import flank.corellium.client.data.AgentOperation
import io.ktor.client.features.websocket.ClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger

class Agent(
    private val connection: ClientWebSocketSession
) {
    private val counter = AtomicInteger(1)
    private val format = Json {}

    suspend fun uploadFile(path: String, bytes: ByteArray) {
        val id = counter.getAndIncrement()
        connection.send(
            Frame.Text(
                format.encodeToString(
                    AgentOperation(
                        type = "file",
                        op = "upload",
                        id = id,
                        path = path
                    )
                )
            )
        )

        val idBytes = ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(id.toByte())
            .array()
        val payload = ByteBuffer.wrap(idBytes + bytes)

        connection.send(Frame.Binary(true, payload))
    }

    suspend fun close() = connection.close()
}
