package flank.exection.parallel

import flank.exection.parallel.internal.CreateTaskFunction
import flank.exection.parallel.internal.Execution
import flank.exection.parallel.internal.invoke
import flank.exection.parallel.internal.lazy
import flank.exection.parallel.internal.reduce
import flank.exection.parallel.internal.validate
import java.lang.System.currentTimeMillis

/**
 * Reduce given [Tasks] by [returns] types to remove unneeded tasks from the graph.
 * The returned graph will contain only tasks that are returning selected types, their dependencies and derived dependencies.
 */
operator fun Tasks.invoke(returns: Set<Parallel.Type<*>>): Tasks = reduce(returns)

/**
 * Execute tasks in parallel with a given args.
 * Before executing the [validate] is performed on a [Tasks] for a given [args] to detect missing dependencies.
 */
infix operator fun Tasks.invoke(args: ParallelState) = Execution(validate(args), args).invoke()

/**
 * The namespace for parallel execution primitives.
 */
object Parallel {

    /**
     * Abstraction for execution data provider which is also an context for task execution.
     * For initialization purpose some properties are exposed as variable.
     */
    open class Context {
        /**
         * The state properties map. Is for initialization purpose, and shouldn't be mutated after initialization.
         */
        internal lateinit var state: ParallelState

        /**
         * Exposed reference to output, accessible by tasks.
         */
        val out: Output by Logger()

        /**
         * Factory method for creating properties from data objects.
         */
        operator fun <T : Any> Type<T>.invoke(): Lazy<T> = lazy(type = this)
    }

    /**
     * Common interface for the tasks arguments and return values.
     */
    interface Type<T : Any>

    /**
     * Structure that is representing single task of execution.
     */
    data class Task<R : Any>(
        val signature: Signature<R>,
        val execute: suspend ParallelState.() -> R
    ) {
        /**
         * The task signature that contains arguments types and returned type.
         */
        data class Signature<R : Any>(
            val returns: Type<R>,
            val args: Set<Type<*>>,
        )

        /**
         * Parameterized factory for creating task functions in scope of [X].
         */
        class Body<X : Context>(override val context: () -> X) : CreateTaskFunction<X>
    }

    data class Event(
        val type: Type<*>,
        val data: Any,
        val timestamp: Long = currentTimeMillis(),
    ) {
        object Start
        object Stop
    }

    object Logger : Type<Output>

    data class ValidationError(
        val data: MissingDependencies
    ) : Exception("Cannot resolve dependencies $data")
}

/**
 * Common signature for structural log output.
 */
internal typealias Output = Any.() -> Unit

/**
 * Immutable state for parallel execution.
 */
internal typealias ParallelState = Map<Parallel.Type<*>, Any>

/**
 * Type for group of parallel tasks. Each task must be unique in group.
 */
internal typealias Tasks = Set<Parallel.Task<*>>

/**
 * The [Map.values] are representing missing dependencies required by the tasks to provide [Map.keys].
 */
internal typealias MissingDependencies = Map<Parallel.Type<*>, Set<Parallel.Type<*>>>
