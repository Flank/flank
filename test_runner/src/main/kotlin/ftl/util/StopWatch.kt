package ftl.util

import ftl.run.exception.FlankGeneralError
import java.util.concurrent.TimeUnit.MILLISECONDS

class StopWatch {

    private var startTime: Long = 0

    fun start(): StopWatch {
        startTime = System.currentTimeMillis()
        return this
    }

    fun check(alignSeconds: Boolean = false): String {
        if (startTime == 0L) throw FlankGeneralError("startTime is zero. start not called")

        val duration = System.currentTimeMillis() - startTime

        val minutes = MILLISECONDS.toMinutes(duration)
        val seconds = MILLISECONDS.toSeconds(duration) % 60

        // align seconds
        // 3m  0s
        // 2m 23s
        val space = if (alignSeconds && seconds < 10)
            "  " else
            " "

        return String.format("%dm$space%ds", minutes, seconds)
    }
}
