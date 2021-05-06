@file:Suppress("EXPERIMENTAL_API_USAGE")

package ftl.run.common

import com.google.testing.model.TestMatrix
import flank.common.logLn
import ftl.args.IArgs
import ftl.client.google.GcTestMatrix
import ftl.run.status.TestMatrixStatusPrinter
import ftl.util.MatrixState
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
    printMatrixStatus: (TestMatrix) -> Unit = TestMatrixStatusPrinter(
        args = args,
        testMatricesIds = testMatricesIds
    )
): Collection<TestMatrix> = coroutineScope {
    testMatricesIds.asFlow().flatMapMerge { testMatrixId ->
        matrixChangesFlow(
            testMatrixId = testMatrixId,
            projectId = args.project
        )
    }.onEach {
        printMatrixStatus(it)
    }.fold(emptyMap<String, TestMatrix>()) { matrices, next ->
        matrices + (next.testMatrixId to next)
    }.values.also {
        logLn()
    }
}

private fun matrixChangesFlow(
    testMatrixId: String,
    projectId: String
): Flow<TestMatrix> = flow {
    while (true) {
        val matrix = GcTestMatrix.refresh(testMatrixId, projectId)
        emit(matrix)
        if (matrix.isCompleted) break else delay(5_000)
    }
}

private val TestMatrix.isCompleted: Boolean
    get() = MatrixState.completed(state) &&
        testExecutions?.all { MatrixState.completed(it.state) } ?: true
