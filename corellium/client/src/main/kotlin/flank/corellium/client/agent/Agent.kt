package flank.corellium.client.agent

import flank.corellium.client.data.CommandResult
import io.ktor.client.features.websocket.ClientWebSocketSession
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

class Agent internal constructor(
    internal val session: ClientWebSocketSession,
    internal val tasks: TasksMap = TasksMap(),
    internal val counter: AtomicInteger = AtomicInteger(1),
    internal val format: Json = Json {},
)

typealias TasksMap = ConcurrentHashMap<Int, (CommandResult) -> Unit>
