package flank.corellium.client.agent

import flank.corellium.client.data.AgentOperation
import flank.corellium.client.data.CommandResult
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal suspend fun Agent.sendCommand(command: AgentOperation) =
    session.send(Frame.Text(format.encodeToString(command)))

internal suspend fun Agent.await(operationId: Int, timeMillis: Long) {
    val task = CompletableDeferred<CommandResult>(Job())
    tasks[operationId] = { task.complete(it) }
    try {
        val error = withTimeout(timeMillis) { task.await() }.error
        if (error != null) throw TaskException(error)
    } finally {
        tasks -= operationId
    }
}

private val format = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
}
