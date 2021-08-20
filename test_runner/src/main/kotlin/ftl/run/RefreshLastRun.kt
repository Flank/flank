package ftl.run

import ftl.api.ShardChunks
import ftl.api.TestMatrix
import ftl.api.refreshTestMatrix
import ftl.args.IArgs
import ftl.domain.RefreshLastRunState
import ftl.domain.testmatrix.needsUpdate
import ftl.domain.testmatrix.updateWithMatrix
import ftl.json.MatrixMap
import ftl.json.updateMatrixMap
import ftl.json.validate
import ftl.presentation.publish
import ftl.reports.util.ReportManager
import ftl.run.common.fetchAllTestRunArtifacts
import ftl.run.common.getLastArgs
import ftl.run.common.getLastMatrices
import ftl.run.common.pollMatrices
import ftl.run.common.updateMatrixFile
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
    fetchAllTestRunArtifacts(matrixMap, lastArgs)

    // Must generate reports *after* fetching xml artifacts since reports require xml
    ReportManager.generate(matrixMap, lastArgs, testShardChunks)

    matrixMap.validate(lastArgs.ignoreFailedTests)
}

/** Refresh all in progress matrices in parallel **/
private suspend fun refreshMatrices(matrixMap: MatrixMap, args: IArgs) = coroutineScope {
    RefreshLastRunState.RefreshMatricesStarted.publish()

    val jobs = arrayListOf<Deferred<TestMatrix.Data>>()
    val map = matrixMap.map
    var matrixCount = 0
    map.forEach { matrix ->
        // Only refresh unfinished
        if (MatrixState.inProgress(matrix.value.state)) {
            matrixCount += 1
            jobs += async(Dispatchers.IO) { refreshTestMatrix(TestMatrix.Identity(matrix.key, args.project, matrix.value.historyId, matrix.value.executionId)) }
        }
    }

    if (matrixCount != 0) {
        RefreshLastRunState.RefreshMatrices(matrixCount).publish()
    }

    var dirty = false
    jobs.awaitAll().forEach { matrix ->
        val matrixId = matrix.matrixId

        RefreshLastRunState.RefreshMatrix(matrix.state, matrixId).publish()

        if (map[matrixId]?.needsUpdate(matrix) == true) {
            map[matrixId]?.updateWithMatrix(matrix)?.let {
                matrixMap.update(matrixId, it)
                dirty = true
            }
        }
    }

    if (dirty) {
        RefreshLastRunState.UpdatingMatrixFile.publish()
        args.updateMatrixFile(matrixMap)
    }
}
