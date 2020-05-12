@file:Suppress("EXPERIMENTAL_API_USAGE")

package ftl.run.common

import com.google.api.services.testing.model.Environment
import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.TestMatrix
import ftl.args.IArgs
import ftl.gc.GcTestMatrix
import ftl.util.StopWatch
import ftl.util.StopWatchMatrix
import ftl.util.isCompleted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce

suspend fun pollMatrices(
    testMatricesIds: Iterable<String>,
    args: IArgs,
    stopWatch: StopWatch = StopWatch()
): List<TestMatrix> = coroutineScope {
    stopWatch.start()
    testMatricesIds.map { testMatrixId ->
        async(Dispatchers.IO) {
            matrixChangesFlow(
                testMatrixId = testMatrixId,
                projectId = args.project
            ).printChanges(
                stopWatch = stopWatch,
                args = args
            ).reduce { _, latest ->
                latest
            }
        }
    }.awaitAll()
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

private fun Flow<TestMatrix>.printChanges(
    stopWatch: StopWatch,
    args: IArgs,
    cache: MutableMap<String, ExecutionStatus> = mutableMapOf()
) = onEach { matrix: TestMatrix ->
    matrix.testExecutions.map { execution ->
        printExecutionStatus(
            watch = StopWatchMatrix(stopWatch, execution.formatName(args)),
            previous = cache[execution.id] ?: ExecutionStatus(),
            current = execution.getDeviceStatus().also {
                cache[execution.id] = it
            }
        )
    }
    if (matrix.isCompleted) StopWatchMatrix(
        stopwatch = stopWatch,
        matrixId = matrix.testMatrixId
    ).puts(matrix.state)
}

private fun printExecutionStatus(
    previous: ExecutionStatus,
    current: ExecutionStatus,
    watch: StopWatchMatrix
) {
    if (current.error != previous.error && current.error != null) {
        watch.puts("Error: ${current.error}")
    }
    current.progress.minus(previous.progress).forEach { message ->
        watch.puts(message)
    }
    if (current.state != previous.state) {
        watch.puts(current.state)
    }
}

private fun TestExecution.formatName(args: IArgs): String {
    val matrixExecutionId = id.split("_")
    val matrixId = matrixExecutionId.first()
    val executionId = matrixExecutionId.takeIf { args.flakyTestAttempts > 0 }?.getOrNull(1)?.let { " $it" } ?: ""
    val env: Environment? = environment
    val device = env?.androidDevice?.androidModelId ?: env?.iosDevice?.iosModelId
    val deviceVersion = env?.androidDevice?.androidVersionId ?: env?.iosDevice?.iosVersionId
    val shard = shard?.takeUnless { args.disableSharding }?.run { " shard-${(shardIndex ?: 0)}" } ?: ""
    return "$matrixId $device-$deviceVersion$shard$executionId"
}

private fun TestExecution.getDeviceStatus() = ExecutionStatus(
    testExecutionId = id,
    state = state,
    error = testDetails?.errorMessage,
    progress = testDetails?.progressMessages ?: emptyList()
)

private data class ExecutionStatus(
    val testExecutionId: String = "",
    val state: String = "",
    val error: String? = null,
    val progress: List<String> = emptyList()
)
