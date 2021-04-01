package ftl.reports

import flank.common.OutputLogLevel
import flank.common.logLn
import ftl.util.Duration
import ftl.util.StopWatch
import ftl.util.formatted

val totalDurationStopWatch by lazy { StopWatch() }

private val stepsTimes = mutableListOf<StepRunDuration>()

data class StepRunDuration(
    val name: String,
    val duration: Duration
)

fun startDurationMeasurement() {
    totalDurationStopWatch.start()
}

fun addStepTime(name: String, duration: Duration) {
    stepsTimes.add(StepRunDuration(name, duration))
}

fun printTotalDuration() {

    logLn(level = OutputLogLevel.DETAILED)
    logLn("Total run duration: ${totalDurationStopWatch.check().formatted(alignSeconds = true)}", level = OutputLogLevel.DETAILED)
    stepsTimes.forEach { stepRunDuration ->
        logLn("\t- ${stepRunDuration.name}: ${stepRunDuration.duration.formatted()}", level = OutputLogLevel.DETAILED)
    }
}
