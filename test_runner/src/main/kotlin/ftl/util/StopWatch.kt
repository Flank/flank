package ftl.util

import ftl.run.exception.FlankGeneralError
import java.util.Locale
import java.util.concurrent.TimeUnit.MILLISECONDS

class StopWatch {

    private var startTime: Long = 0

    fun start(): StopWatch {
        startTime = System.currentTimeMillis()
        return this
    }

    fun check(): Duration {
        if (startTime == 0L) throw FlankGeneralError("startTime is zero. start not called")

        return Duration(System.currentTimeMillis() - startTime)
    }
}

suspend fun measureTime(block: suspend () -> Unit) = StopWatch().run {
    start()
    block()
    check()
}

data class Duration(val seconds: Long)

fun Duration.formatted(alignSeconds: Boolean = false): String {
    val minutes = MILLISECONDS.toMinutes(seconds)
    val seconds = MILLISECONDS.toSeconds(seconds) % 60

    // align seconds
    // 3m  0s
    // 2m 23s
    val space = if (alignSeconds && seconds < 10)
        "  " else
        " "

    return String.format(Locale.getDefault(), "%dm$space%ds", minutes, seconds)
}
