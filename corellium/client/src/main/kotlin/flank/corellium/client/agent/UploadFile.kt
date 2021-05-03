package flank.corellium.client.agent

import flank.corellium.client.data.AgentOperation
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import java.nio.ByteBuffer
import java.nio.ByteOrder

suspend fun Agent.uploadFile(path: String, bytes: ByteArray) {
    val id = counter.getAndIncrement()

    sendCommand(
        AgentOperation(
            type = "file",
            op = "upload",
            id = id,
            path = path
        )
    )

    val idBytes = ByteBuffer.allocate(8)
        .order(ByteOrder.LITTLE_ENDIAN)
        .put(id.toByte())
        .array()

    val payload = ByteBuffer.wrap(idBytes + bytes).order(ByteOrder.LITTLE_ENDIAN)

    session.send(Frame.Binary(true, payload))
    session.send(Frame.Binary(true, idBytes))

    val task = Job()
    tasks[id] = defaultResultHandler(task)
    withTimeout(100_000) {
        task.join()
    }
}
