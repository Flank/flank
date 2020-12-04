package ftl.util

import ftl.config.FtlConstants
import ftl.log.logLn

class StopWatchMatrix(private val stopwatch: StopWatch, private val matrixId: String) {

    fun puts(msg: String) {
        val timestamp = stopwatch.check(alignSeconds = true)
        logLn("${FtlConstants.indent}$timestamp $matrixId $msg")
    }
}
