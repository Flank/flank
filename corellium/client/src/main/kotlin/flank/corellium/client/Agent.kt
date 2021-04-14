package flank.corellium.client

import flank.corellium.client.data.AgentOperation
import flank.corellium.client.data.CommandResult
import flank.corellium.client.logging.LoggingLevel
import flank.corellium.client.util.withProgress
import io.ktor.client.HttpClient
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.ClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Agent(
    private val agentUrl: String,
    private val logging: LoggingLevel,
    private val token: String
) {
    private lateinit var connection: ClientWebSocketSession

    private val counter = AtomicInteger(1)
    private val format = Json {}
    private val tasks = ConcurrentHashMap<Int, (CommandResult) -> Unit>()
    private var uploading = false

    private val wsClient = HttpClient {
        install(WebSockets)
        install(Logging) {
            level = logging.level
        }
    }

    private suspend fun connect() {
        connection = wsClient.webSocketSession {
            url(agentUrl)
            header("Authorization", token)
        }.apply {
            launch {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> textFrameHandler(frame)
                        is Frame.Ping -> println("got ping")
                        is Frame.Pong -> println("got pong")
                        else -> println(frame.data.decodeToString())
                    }
                }
            }
        }
    }

    private fun textFrameHandler(frame: Frame.Text) {
        println("\nReceived: ${frame.readText()}")
        val result = format.decodeFromString<CommandResult>(frame.readText())
        tasks[result.id]?.let { it(result) }
    }

    private suspend fun isReady() {
        val task = Job()
        val id = counter.getAndIncrement()
        connection.sendCommand(
            AgentOperation(
                type = "app",
                op = "ready",
                id = id
            )
        )
        tasks[id] = getCommonHandler(task)
        withTimeout(20_000) {
            task.join()
        }
    }

    suspend fun waitForAgentReady() = withProgress {
        var booting: Boolean
        do {
            delay(20_000)
            booting = try {
                connect()
                isReady()
                false
            } catch (ex: Exception) {
                true
            }
        } while (booting)
    }

    suspend fun uploadFile(path: String, bytes: ByteArray) {
        val id = counter.getAndIncrement()
        connection.sendCommand(
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

        connection.send(Frame.Binary(true, payload))
        connection.send(Frame.Binary(true, idBytes))

        val task = Job()
        tasks[id] = getUploadHandler(task)
        withTimeoutOrNull(10_000) {
            task.join()
        }
    }

    suspend fun close() = connection.close()

    private suspend fun ClientWebSocketSession.sendCommand(command: AgentOperation) =
        send(Frame.Text(format.encodeToString(command)))

    private fun getCommonHandler(task: CompletableJob): (CommandResult) -> Unit = {
        if (it.success) task.complete()
        else {
            println("Task ${it.id} failed")
            println(it)
        }
    }

    private fun getUploadHandler(task: CompletableJob): (CommandResult) -> Unit = {
        if (it.success) {
            task.complete()
            uploading = false
        } else {
            println("Task ${it.id} failed")
            println(it)
        }
    }
}
