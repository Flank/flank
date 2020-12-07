package ftl.util

import ftl.log.OutputLogLevel
import ftl.log.log
import ftl.log.logLn
import java.util.Timer
import java.util.TimerTask

class ProgressBar {
    private val task = ProgressBarTask()
    private val timer = Timer(true)

    fun start(msg: String) {
        log("  $msg ", OutputLogLevel.DETAILED)
        timer.scheduleAtFixedRate(task, 0, 10_000)
    }

    fun stop() {
        logLn(level = OutputLogLevel.DETAILED)
        timer.cancel()
        task.cancel()
    }
}

private class ProgressBarTask : TimerTask() {
    override fun run() {
        log(".", OutputLogLevel.DETAILED)
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
