package flank.exection.parallel.internal

import flank.exection.parallel.Output
import flank.exection.parallel.Parallel.Event
import flank.exection.parallel.Parallel.Logger
import flank.exection.parallel.Parallel.Task
import flank.exection.parallel.Parallel.Type
import flank.exection.parallel.ParallelState
import flank.exection.parallel.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

/**
 * Invoke the given [Execution] in parallel.
 */
internal operator fun Execution.invoke(): Flow<ParallelState> =
    channel.consumeAsFlow()

        // Abort the execution if any task was failed and returned the throwable value.
        .onEach { (type, value) -> if (value is Throwable && isNotClosed()) abortBy(type, value) }

        // Accumulate each received value in state.
        .scan(initial) { state, value -> state + value }

        // Handle state changes.
        .onEach { state: ParallelState ->
            when {
                // State is containing all required types. Close the channel which will finish the execution.
                isComplete(state) -> channel.close()

                // The execution is already finished and just collecting the rest of properties. Nothing to do here.
                isFinished(state) -> Unit

                // For each task group that is satisfied by current state.
                else -> filterTasksFor(state).forEach { (args, tasks) ->

                    // Prepare common properties for current iteration.
                    val properties = state.filterKeys(args::contains)

                    // Execute tasks with required properties.
                    tasks.forEach { task: Task<*> -> execute(task, properties) }
                }
            }
        }

        // Cancel the coroutine scope for tasks.
        .onCompletion { scope.cancel() }

        // Drop first element which is always the initial state
        .drop(1)

/**
 * Internal context of the tasks execution.
 */
internal class Execution(
    /**
     * List of tasks supposed to run in parallel.
     */
    tasks: Tasks,
    /**
     * The initial state required to satisfy the tasks.
     */
    val initial: ParallelState,
) {
    /**
     * Coroutine scope for executing tasks.
     */
    val scope = CoroutineScope(Job() + Dispatchers.IO)

    /**
     * Map of jobs (tasks) that are resolving values for associated types.
     */
    val jobs = mutableMapOf<Type<*>, Job>()

    /**
     * Channel for receiving resolved values from tasks.
     */
    val channel = Channel<Pair<Type<*>, Any>>(Channel.BUFFERED)

    /**
     * The set of value types required to complete the execution.
     */
    val required = tasks.map { task -> task.signature.returns }.toSet()

    /**
     * Map of remaining tasks for run grouped by arguments.
     */
    val remaining = tasks.groupBy { task -> task.signature.args }.toMutableMap()

    /**
     * Reference to optional output for structural logs.
     */
    @Suppress("UNCHECKED_CAST")
    val output = initial[Logger] as? Output
}

// ========================== Private functions ==========================

/**
 * The execution is not closed if the properties channel is not closed for send.
 * Other words, task can still send property to update the state.
 */
private fun Execution.isNotClosed(): Boolean = !channel.isClosedForSend

/**
 * Cancel running jobs, update state for cancellation values and close the execution.
 */
private suspend fun Execution.abortBy(type: Type<*>, cause: Throwable) {

    // Abort running jobs.
    scope.cancel("aborted by ${type.javaClass.name}", cause)
    scope.coroutineContext[Job]?.join()

    // Add remaining tasks to state.
    remaining.toMap().values.flatten().forEach { task ->
        channel.trySend(task.signature.returns to task)
    }

    // Close execution channel.
    channel.close()
}

/**
 * The execution is complete when all required types was accumulated to state.
 */
private fun Execution.isComplete(state: ParallelState): Boolean =
    state.keys.containsAll(required)

/**
 * Execution is finished when any value is [Throwable] or there are no remaining [Task] to run.
 */
private fun Execution.isFinished(state: ParallelState): Boolean =
    state.any { (_, value) -> value is Throwable } || remaining.isEmpty()

/**
 * Get tasks available to run in the current state.
 *
 * @throws IllegalStateException If there are no tasks to run and no running jobs for a given state.
 */
private fun Execution.filterTasksFor(state: ParallelState): Map<Set<Type<*>>, List<Task<*>>> =
    remaining.filterKeys(state.keys::containsAll).apply {

        // Check if execution is not detached, other words some of tasks cannot be run because of missing values in state.
        // Typically will fail if broken graph was not validated before execution.
        isNotEmpty() || // Found tasks to execute.
            jobs.any { (_, job) -> job.isActive } || // some jobs still are running, is required to wait for values from them.
            channel.isEmpty.not() || // some values are waiting in the channel queue.
            throw DeadlockError(state, jobs, remaining)

        // Remove from queue the tasks for current iteration.
        remaining -= keys
    }

/**
 * Typically can occurs when execution is running on broken graph of tasks.
 */
private data class DeadlockError(
    val state: ParallelState,
    val jobs: Map<Type<*>, Job>,
    val remaining: Map<Set<Type<*>>, List<Task<*>>>,
) : Error() {
    override fun toString(): String = listOf(
        "Parallel execution dump:",
        state,
        jobs,
        remaining,
        "", super.toString(),
    ).joinToString("\n")
}

/**
 * Execute given tasks as coroutine job.
 */
private fun Execution.execute(
    task: Task<*>,
    args: Map<Type<*>, Any>,
) {
    // Obtain return type for current task.
    val type: Type<*> = task.signature.returns

    // Keep task job.
    jobs += type to scope.launch {

        // Extend root output for adding additional data.
        val out: Output = output?.let { { it(Event(type, this)) } } ?: {}

        // Log the task was started
        Event.Start.out()

        // Try to execute tasks.
        val (result, eventFinish) = try {

            // Prepare context for task
            val context = initial + args + (Logger to out)

            // Execute task on given state
            task.execute(context) to Event.Stop
        } catch (throwable: Throwable) {
            throwable to throwable
        }

        // Log the task was finished.
        eventFinish.out()

        // Feed up the execution.
        channel.trySend(type to result)
    }
}
