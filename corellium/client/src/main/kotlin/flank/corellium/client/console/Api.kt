package flank.corellium.client.console

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

suspend fun Console.sendCommand(command: String): Unit =
    session.send(Frame.Binary(true, (command + "\n").encodeToByteArray()))

suspend fun Console.waitForIdle(timeToWait: Long) {
    delay(3_000)
    while (System.currentTimeMillis() - lastResponseTime < timeToWait) delay(200)
}

suspend fun Console.close(): Unit = session.close()

@Deprecated("Use Console.flowLogs()")
fun Console.launchOutputPrinter() = session.launch {
    // drop console bash history which is received as first frame
    session.incoming.receive()

    for (frame in session.incoming) {
        lastResponseTime = System.currentTimeMillis()
        print(frame.data.decodeToString())
    }
}

suspend fun Console.clear() {
    session.incoming.receive()
}

fun Console.flowLogs(): Flow<String> = session.incoming
    .receiveAsFlow()
    .onEach { lastResponseTime = System.currentTimeMillis() }
    .map { frame: Frame -> frame.data.decodeToString() }
    .normalizeLines()

internal fun Flow<String>.normalizeLines(): Flow<String> {
    var acc = ""
    return transform { next ->
        acc += next
        acc.split("\n").let { list ->
            acc = list.last()
            list.dropLast(1).forEach {
                emit(it)
            }
        }
    }
}
