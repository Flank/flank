package ftl.util

import ftl.log.logHigh
import ftl.log.logLineHigh
import java.util.Timer
import java.util.TimerTask

class ProgressBar {
    private val task = ProgressBarTask()
    private val timer = Timer(true)

    fun start(msg: String) {
        logHigh("  $msg ")
        timer.scheduleAtFixedRate(task, 0, 10_000)
    }

    fun stop() {
        logLineHigh()
        timer.cancel()
        task.cancel()
    }
}

private class ProgressBarTask : TimerTask() {
    override fun run() {
        logHigh(".")
    }
}

fun runWithProgress(
    startMessage: String,
    action: () -> Unit,
    onError: (Exception) -> Unit
) {
    val progress = ProgressBar()
    try {
        progress.start(startMessage)
        action()
    } catch (e: Exception) {
        onError(e)
    } finally {
        progress.stop()
    }
}
