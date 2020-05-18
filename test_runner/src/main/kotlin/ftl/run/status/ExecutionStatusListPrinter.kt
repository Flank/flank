package ftl.run.status

import com.google.api.services.testing.model.Environment
import com.google.api.services.testing.model.TestExecution
import ftl.args.IArgs

class ExecutionStatusListPrinter(
    private val args: IArgs,
    private val executionsCache: LinkedHashMap<String, ExecutionStatus> = LinkedHashMap(),
    private val printExecutionStatues: (List<ExecutionStatus.Change>) -> Unit = createExecutionStatusPrinter(args)
) : (String, List<TestExecution>?) -> Unit {
    override fun invoke(
        time: String,
        executions: List<TestExecution>?
    ) = getChanges(time, executions).let(printExecutionStatues)

    private fun getChanges(
        time: String,
        executions: List<TestExecution>?
    ): List<ExecutionStatus.Change> = executions?.map { execution ->
        ExecutionStatus.Change(
            name = execution.formatName(args),
            previous = executionsCache[execution.id] ?: ExecutionStatus(),
            current = execution.executionStatus().also { status ->
                executionsCache[execution.id] = status
            },
            time = time
        )
    } ?: emptyList()
}

private fun TestExecution.formatName(args: IArgs): String {
    val matrixExecutionId = id?.split("_")
    val matrixId = matrixExecutionId?.first()
    val executionId = matrixExecutionId?.takeIf { args.flakyTestAttempts > 0 }?.getOrNull(1)?.let { " $it" } ?: ""
    val env: Environment? = environment
    val device = env?.androidDevice?.androidModelId ?: env?.iosDevice?.iosModelId
    val deviceVersion = env?.androidDevice?.androidVersionId ?: env?.iosDevice?.iosVersionId
    val shard = shard?.takeUnless { args.disableSharding }?.run { " shard-${(shardIndex ?: 0)}" } ?: ""
    return "$matrixId $device-$deviceVersion$shard$executionId"
}

private fun TestExecution.executionStatus() = ExecutionStatus(
    state = state ?: "UNKNOWN",
    error = testDetails?.errorMessage,
    progress = testDetails?.progressMessages ?: emptyList()
)
