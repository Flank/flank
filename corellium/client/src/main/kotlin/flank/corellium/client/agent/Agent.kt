package flank.corellium.client.agent

import flank.corellium.client.data.CommandResult
import io.ktor.client.features.websocket.ClientWebSocketSession
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * The context of connection with the instance agent.
 *
 * @property session Web socket session with the agent.
 * @property tasks References to the running task result listeners.
 * @property counter Task counter.
 * @property format [Json] helper for parsing and formatting.
 */
class Agent internal constructor(
    internal val session: ClientWebSocketSession,
    internal val tasks: TasksMap = TasksMap(),
    internal val counter: AtomicInteger = AtomicInteger(1),
    internal val format: Json = Json {},
)

internal typealias TasksMap = ConcurrentHashMap<Int, (CommandResult) -> Unit>
