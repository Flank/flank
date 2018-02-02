package ftl.util

import java.util.concurrent.TimeUnit

class StopWatch {

    private var startTime: Long = 0

    fun start(): StopWatch {
        startTime = System.currentTimeMillis()
        return this
    }

    /** ms to minutes  */
    private fun minutes(duration: Long): Long {
        return TimeUnit.MILLISECONDS.toMinutes(duration)
    }

    /** ms to seconds  */
    private fun seconds(duration: Long): Long {
        return TimeUnit.MILLISECONDS.toSeconds(duration)
    }

    fun check(indent: Boolean = false): String {
        if (startTime == 0L) {
            throw RuntimeException("startTime is zero. start not called")
        }
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        val minutes = minutes(duration)
        val seconds = seconds(duration) % 60

        // align seconds
        // 3m  0s
        // 2m 23s
        var space = " "
        if (indent && seconds < 10) space = "  "

        return String.format("%dm$space%ds", minutes, seconds)
    }

    fun reset(): StopWatch {
        startTime = 0
        return this
    }
}
