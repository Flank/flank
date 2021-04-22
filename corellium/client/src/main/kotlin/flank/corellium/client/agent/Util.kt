package flank.corellium.client.agent

import flank.corellium.client.data.AgentOperation
import flank.corellium.client.data.CommandResult
import io.ktor.http.cio.websocket.Frame
import kotlinx.coroutines.CompletableJob
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal fun defaultResultHandler(task: CompletableJob): (CommandResult) -> Unit = { result ->
    if (result.success) task.complete() else {
        println("Task ${result.id} failed")
        println(format.encodeToString(result))
    }
}

internal suspend fun Agent.sendCommand(command: AgentOperation) =
    session.send(Frame.Text(format.encodeToString(command)))

private val format = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
}
