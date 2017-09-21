import java.util.concurrent.TimeUnit

class StopWatch {

    private var startTime: Long = 0
    private var endTime: Long = 0

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

    fun end(): String {
        endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        val minutes = minutes(duration)
        val seconds = seconds(duration) % 60

        return String.format("%dm %ds", minutes, seconds)
    }

    fun reset(): StopWatch {
        startTime = 0
        endTime = 0
        return this
    }
}
