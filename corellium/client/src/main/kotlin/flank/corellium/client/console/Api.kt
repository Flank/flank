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

/**
 * Send a command to serial console.
 */
suspend fun Console.sendCommand(command: String): Unit =
    session.send(Frame.Binary(true, (command + "\n").encodeToByteArray()))

/**
 * Block the execution and wait until the console is not producing output for a specified amount of time.
 */
suspend fun Console.waitForIdle(timeToWait: Long) {
    delay(3_000)
    while (System.currentTimeMillis() - lastResponseTime < timeToWait) delay(200)
}

/**
 * Close the console connection.
 */
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

/**
 * Clear unread log messages buffer.
 */
suspend fun Console.clear() {
    session.incoming.receive()
}

/**
 * Flow log lines from the serial console output.
 *
 * Do not collect more than one flow from the same [Console] at the same time to prevent a race condition.
 */
fun Console.flowLogs(): Flow<String> = session.incoming
    .receiveAsFlow()
    .onEach { lastResponseTime = System.currentTimeMillis() }
    .map { frame: Frame -> frame.data.decodeToString() }
    .normalizeLines()

/**
 * Converts flow of frames into flow of lines.
 *
 * The incoming console logs are divided into frames for network purposes, but lines are much more convenient to parse.
 *
 * To check the example, see the unit test of this method.
 */
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
