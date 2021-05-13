package ftl.run.status

import ftl.api.TestMatrix
import ftl.args.IArgs

class ExecutionStatusListPrinter(
    private val args: IArgs,
    private val executionsCache: LinkedHashMap<String, ExecutionStatus> = LinkedHashMap(),
    private val printExecutionStatues: (List<ExecutionStatus.Change>) -> Unit = createExecutionStatusPrinter(args)
) : (String, List<TestMatrix.TestExecution>) -> Unit {
    override fun invoke(
        time: String,
        executions: List<TestMatrix.TestExecution>
    ) = getChanges(time, executions).let(printExecutionStatues)

    private fun getChanges(
        time: String,
        executions: List<TestMatrix.TestExecution>
    ): List<ExecutionStatus.Change> = executions.map { execution ->
        ExecutionStatus.Change(
            name = execution.formatName(args),
            previous = executionsCache[execution.id] ?: ExecutionStatus(),
            current = execution.executionStatus().also { status ->
                executionsCache[execution.id] = status
            },
            time = time
        )
    }
}

private fun TestMatrix.TestExecution.formatName(args: IArgs): String {
    val matrixExecutionId = id.split("_")
    val matrixId = matrixExecutionId.first()
    val executionId = matrixExecutionId.takeIf { args.flakyTestAttempts > 0 }?.getOrNull(1)?.let { " $it" } ?: ""
    val shard = shardIndex?.takeUnless { args.disableSharding }?.run { " shard-${(shardIndex)}" } ?: ""
    return "$matrixId $modelId-$deviceVersion$shard$executionId"
}

private fun TestMatrix.TestExecution.executionStatus() = ExecutionStatus(
    state = state,
    error = errorMessage,
    progress = progress
)
