package flank.corellium.client.agent

import flank.corellium.client.data.AgentOperation
import flank.corellium.client.data.CommandResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString

/**
 * Internal factory method for creating initialized [Agent] connection context.
 */
internal suspend fun connectAgent(
    agentUrl: String,
    token: String,
    logLevel: LogLevel = LogLevel.NONE
): Agent = run {
    val client = HttpClient(CIO) {
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
                .apply { handleIncomingFrames() }
                .waitForReady()
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

private suspend fun Agent.waitForReady() = apply {
    val id = counter.getAndIncrement()
    sendCommand(
        AgentOperation(
            type = "app",
            op = "ready",
            id = id
        )
    )
    await(id, 20_000)
}

private fun Agent.handleIncomingFrames() =
    session.launch {
        for (frame in session.incoming) {
            when (frame) {
                is Frame.Text -> handleTestFrame(frame)
                is Frame.Ping -> println("got ping")
                is Frame.Pong -> println("got pong")
                else -> Unit
            }
        }
    }

private suspend fun Agent.handleTestFrame(frame: Frame.Text) {
    val result = format.decodeFromString<CommandResult>(frame.readText())
    tasks[result.id]?.invoke(result)
}
