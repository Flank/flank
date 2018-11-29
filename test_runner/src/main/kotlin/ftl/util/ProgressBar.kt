package ftl.util

import java.util.Timer
import java.util.TimerTask

class ProgressBar {
    private val task = ProgressBarTask()
    private val timer = Timer(true)

    fun start(msg: String) {
        print("  $msg ")
        timer.scheduleAtFixedRate(task, 0, 10_000)
    }

    fun stop() {
        println()
        timer.cancel()
        task.cancel()
    }
}

private class ProgressBarTask : TimerTask() {
    override fun run() {
        print(".")
    }
}
