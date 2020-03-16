package ftl.run

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTestMatrix
import ftl.json.SavedMatrix
import ftl.run.common.getLastArgs
import ftl.run.common.getLastMatrices
import ftl.util.MatrixState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// used to cancel and update results from an async run
fun cancelLastRun(args: IArgs) {
    runBlocking {
        val matrixMap = getLastMatrices(args)
        val lastArgs = getLastArgs(args)

        cancelMatrices(matrixMap.map, lastArgs.project)
    }
}

/** Cancel all in progress matrices in parallel **/
suspend fun cancelMatrices(matrixMap: Map<String, SavedMatrix>, project: String) = coroutineScope {
    println("CancelMatrices")

    var matrixCount = 0

    matrixMap.forEach { matrix ->
        // Only cancel unfinished
        if (MatrixState.inProgress(matrix.value.state)) {
            matrixCount += 1
            launch { GcTestMatrix.cancel(matrix.key, project) }
        }
    }

    if (matrixCount == 0) {
        println(FtlConstants.indent + "No matrices to cancel")
    } else {
        println(FtlConstants.indent + "Cancelling ${matrixCount}x matrices")
    }

    println()
}
