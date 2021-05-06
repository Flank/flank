package flank.corellium.client.agent

import flank.corellium.client.data.AgentOperation
import flank.corellium.client.data.CommandResult
import io.ktor.client.HttpClient
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.decodeFromString

suspend fun connectAgent(
    agentUrl: String,
    token: String,
    logLevel: LogLevel = LogLevel.NONE
): Agent = run {
    val client = HttpClient {
        install(WebSockets)
        install(Logging) {
            level = logLevel
        }
    }

    var connection: Agent? = null
    var retryCount = 10
    do {
        require(retryCount-- > 0) { "Max retry count reached" }
        try {
            connection = client
                .createSession(agentUrl, token)
                .let(::Agent)
                .apply {
                    handleIncomingFrames()
                    waitForReady()
                }
        } catch (ex: Exception) {
            delay(20_000)
        }
    } while (connection == null)

    connection
}

private suspend fun HttpClient.createSession(
    agentUrl: String,
    token: String
): DefaultClientWebSocketSession =
    webSocketSession {
        url(agentUrl)
        header("Authorization", token)
    }

private suspend fun Agent.waitForReady() {
    val task = Job()
    val id = counter.getAndIncrement()
    sendCommand(
        AgentOperation(
            type = "app",
            op = "ready",
            id = id
        )
    )
    tasks[id] = { result ->
        if (result.success) task.complete() else {
            println("Task ${result.id} failed")
            println(result)
        }
    }
    withTimeout(20_000) {
        task.join()
    }
}

private fun Agent.handleIncomingFrames() =
    session.launch {
        for (frame in session.incoming) {
            when (frame) {
                is Frame.Text -> handleTestFrame(frame)
                is Frame.Ping -> println("got ping")
                is Frame.Pong -> println("got pong")
                else -> println(frame.data.decodeToString())
            }
        }
    }

private fun Agent.handleTestFrame(frame: Frame.Text) {
    println("Received: ${frame.readText()}")
    val result = format.decodeFromString<CommandResult>(frame.readText())
    tasks[result.id]?.invoke(result)
}
