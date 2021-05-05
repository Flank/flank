@file:Suppress("EXPERIMENTAL_API_USAGE")

package ftl.run.common

import flank.common.logLn
import ftl.api.TestMatrix
import ftl.api.refreshTestMatrix
import ftl.args.IArgs
import ftl.run.status.TestMatrixStatusPrinter
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.onEach

suspend fun pollMatrices(
    testMatricesIds: Iterable<String>,
    args: IArgs,
    printMatrixStatus: (TestMatrix.Data) -> Unit = TestMatrixStatusPrinter(
        args = args,
        testMatricesIds = testMatricesIds
    )
): Collection<TestMatrix.Data> = coroutineScope {
    testMatricesIds.asFlow().flatMapMerge { testMatrixId ->
        matrixChangesFlow(
            testMatrixId = testMatrixId,
            projectId = args.project
        )
    }.onEach {
        printMatrixStatus(it)
    }.fold(emptyMap<String, TestMatrix.Data>()) { matrices, next ->
        matrices + (next.matrixId to next)
    }.values.also {
        logLn()
    }
}

private fun matrixChangesFlow(
    testMatrixId: String,
    projectId: String
): Flow<TestMatrix.Data> = flow {
    while (true) {
        val matrix = refreshTestMatrix(TestMatrix.Identity(testMatrixId, projectId))
        emit(matrix)
        if (matrix.isCompleted) break else delay(5_000)
    }
}
