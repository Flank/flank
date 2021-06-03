package flank.exection.parallel.internal

import flank.exection.parallel.Output
import flank.exection.parallel.Parallel.Context
import flank.exection.parallel.Parallel.Event
import flank.exection.parallel.Parallel.OutputType
import flank.exection.parallel.Parallel.Task
import flank.exection.parallel.Parallel.Type
import flank.exection.parallel.ParallelState
import flank.exection.parallel.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

internal suspend fun <C : Context> execute(
    context: C,
    initialState: ParallelState,
    tasks: Tasks<C>,
): ParallelState = coroutineScope {
    val jobs = mutableMapOf<Type<out Any>, Job>()
    val remaining = ConcurrentHashMap(tasks.groupBy { step -> step.signature.args })
    val channel = Channel<Value<out Any>>(Channel.BUFFERED)
    val returnSignatures = tasks.map { it.signature.returns }
    channel.consumeAsFlow().scan(initialState, merge).onEach { state ->
        if (state.keys.containsAll(returnSignatures)) channel.close()
        remaining.filterKeys(state.keys::containsAll).apply { remaining -= keys }
            .values.flatten().forEach { task: Task<C, *> ->
                val type: Type<out Any> = task.signature.returns
                jobs += type to launch(Dispatchers.IO) {
                    val out: Output = { context.out(Event(type, this)) }
                    try {
                        Event.Start.out()
                        val result = task.execute(context)(state + (OutputType to out))
                        Event.Stop.out()
                        channel.trySend(type to result)
                    } catch (throwable: Throwable) {
                        throwable.out()
                        channel.trySend(type to throwable)
                        val queued = remaining.toMap().also { remaining.clear() }
                        jobs.minus(type).filter { it.value.isActive }.forEach { (type, job) ->
                            job.cancel("aborted")
                            channel.trySend(type to job)
                        }
                        queued.values.flatten().forEach {
                            channel.trySend(it.signature.returns to it)
                        }
                    }
                }
            }
    }.last()
}

private val merge: suspend (ParallelState, Value<out Any>) -> Map<Type<*>, Any> =
    { state, prop -> state + prop }

private typealias Value<T> = Pair<Type<out T>, Any>
