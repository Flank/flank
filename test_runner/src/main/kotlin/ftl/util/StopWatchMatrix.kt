package ftl.util

import ftl.config.FtlConstants

class StopWatchMatrix(private val stopwatch: StopWatch, private val matrixId: String) {

    fun puts(msg: String) {
        val timestamp = stopwatch.check(alignSeconds = true)
        println("${FtlConstants.indent}$timestamp $matrixId $msg")
    }
}
