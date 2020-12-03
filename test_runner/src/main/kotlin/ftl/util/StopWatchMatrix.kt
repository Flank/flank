package ftl.util

import ftl.config.FtlConstants
import ftl.log.logLine

class StopWatchMatrix(private val stopwatch: StopWatch, private val matrixId: String) {

    fun puts(msg: String) {
        val timestamp = stopwatch.check(alignSeconds = true)
        logLine("${FtlConstants.indent}$timestamp $matrixId $msg")
    }
}
