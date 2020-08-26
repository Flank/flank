package ftl.run

import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.gc.GcTestMatrix
import ftl.json.MatrixMap
import ftl.json.updateMatrixMap
import ftl.reports.util.ReportManager
import ftl.run.common.fetchArtifacts
import ftl.run.common.getLastArgs
import ftl.run.common.getLastMatrices
import ftl.run.common.pollMatrices
import ftl.run.common.updateMatrixFile
import ftl.args.ShardChunks
import ftl.json.needsUpdate
import ftl.json.updateWithMatrix
import ftl.util.MatrixState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// used to update and poll the results from an async run
suspend fun refreshLastRun(currentArgs: IArgs, testShardChunks: ShardChunks) {
    val matrixMap = getLastMatrices(currentArgs)
    val lastArgs = getLastArgs(currentArgs)

    refreshMatrices(matrixMap, lastArgs)
    pollMatrices(matrixMap.map.keys, lastArgs).updateMatrixMap(matrixMap)
    fetchArtifacts(matrixMap, lastArgs)

    // Must generate reports *after* fetching xml artifacts since reports require xml
    ReportManager.generate(matrixMap, lastArgs, testShardChunks)
}

/** Refresh all in progress matrices in parallel **/
private suspend fun refreshMatrices(matrixMap: MatrixMap, args: IArgs) = coroutineScope {
    println("RefreshMatrices")

    val jobs = arrayListOf<Deferred<TestMatrix>>()
    val map = matrixMap.map
    var matrixCount = 0
    map.forEach { matrix ->
        // Only refresh unfinished
        if (MatrixState.inProgress(matrix.value.state)) {
            matrixCount += 1
            jobs += async(Dispatchers.IO) { GcTestMatrix.refresh(matrix.key, args.project) }
        }
    }

    if (matrixCount != 0) {
        println(FtlConstants.indent + "Refreshing ${matrixCount}x matrices")
    }

    var dirty = false
    jobs.awaitAll().forEach { matrix ->
        val matrixId = matrix.testMatrixId

        println(FtlConstants.indent + "${matrix.state} $matrixId")

        if (map[matrixId]?.needsUpdate(matrix) == true) {
            map[matrixId]?.updateWithMatrix(matrix)?.let {
                matrixMap.update(matrixId, it)
                dirty = true
            }
        }
    }

    if (dirty) {
        println(FtlConstants.indent + "Updating matrix file")
        updateMatrixFile(matrixMap, args)
    }
    println()
}
