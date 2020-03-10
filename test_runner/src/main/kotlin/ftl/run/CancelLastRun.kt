package ftl.run

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.run.common.getLastArgs
import ftl.run.common.getLastMatrices
import ftl.util.MatrixState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// used to cancel and update results from an async run
fun cancelLastRun(args: IArgs) {
    val matrixMap = getLastMatrices(args)
    val lastArgs = getLastArgs(args)

    cancelMatrices(matrixMap, lastArgs)
}

/** Cancel all in progress matrices in parallel **/
private fun cancelMatrices(matrixMap: MatrixMap, args: IArgs) {
    println("CancelMatrices")

    val map = matrixMap.map
    var matrixCount = 0

    runBlocking {
        map.forEach { matrix ->
            // Only cancel unfinished
            if (MatrixState.inProgress(matrix.value.state)) {
                matrixCount += 1
                launch { GcTestMatrix.cancel(matrix.key, args) }
            }
        }
    }

    if (matrixCount == 0) {
        println(FtlConstants.indent + "No matrices to cancel")
    } else {
        println(FtlConstants.indent + "Cancelling ${matrixCount}x matrices")
    }

    println()
}
