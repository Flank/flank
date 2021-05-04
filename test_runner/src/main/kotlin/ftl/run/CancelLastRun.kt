package ftl.run

import flank.common.logLn
import ftl.api.TestMatrix
import ftl.api.cancelTestMatrix
import ftl.args.IArgs
import ftl.client.google.GcTestMatrix
import ftl.config.FtlConstants
import ftl.json.SavedMatrix
import ftl.run.common.getLastArgs
import ftl.run.common.getLastMatrices
import ftl.util.MatrixState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// used to cancel and update results from an async run
fun cancelLastRun(args: IArgs): MatrixCancelStatus = runBlocking {
    val matrixMap = getLastMatrices(args)
    val lastArgs = getLastArgs(args)

    cancelMatrices(matrixMap.map, lastArgs.project)
}

/** Cancel all in progress matrices in parallel **/
suspend fun cancelMatrices(
    matrixMap: Map<String, TestMatrix.Data>,
    project: String
): MatrixCancelStatus = coroutineScope {
    logLn("CancelMatrices")

    return@coroutineScope matrixMap
        .filter { matrix -> MatrixState.inProgress(matrix.value.state) }
        .map { (matrixKey, _) -> matrixKey }
        .onEach { matrixKey -> launch { cancelTestMatrix(TestMatrix.Identity(matrixKey, project)) } }
        .toMatrixCancelStatus()
}

private fun List<String>.toMatrixCancelStatus(): MatrixCancelStatus = if (size == 0) {
    MatrixCancelStatus.NoMatricesToCancel
} else {
    MatrixCancelStatus.MatricesCanceled(size)
}

sealed class MatrixCancelStatus {
    object NoMatricesToCancel : MatrixCancelStatus()
    data class MatricesCanceled(val count: Int) : MatrixCancelStatus()
}
