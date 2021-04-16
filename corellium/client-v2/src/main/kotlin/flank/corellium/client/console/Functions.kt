package flank.corellium.client.console

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.delay

suspend fun Console.sendCommand(command: String) =
    session.send(Frame.Binary(true, (command + "\n").encodeToByteArray()))

suspend fun Console.waitForIdle(timeToWait: Long) {
    while (System.currentTimeMillis() - lastResponseTime.get() < timeToWait) delay(100)
}

suspend fun Console.close() = session.close()
