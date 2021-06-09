package flank.exection.parallel

import flank.exection.parallel.internal.CreateTaskFunction
import flank.exection.parallel.internal.EagerProperties
import flank.exection.parallel.internal.Execution
import flank.exection.parallel.internal.initialValidators
import flank.exection.parallel.internal.invoke
import flank.exection.parallel.internal.lazyProperty
import flank.exection.parallel.internal.reduceTo
import flank.exection.parallel.internal.validate
import kotlinx.coroutines.flow.Flow
import java.lang.System.currentTimeMillis

// ======================= Core functions =======================

/**
 * Reduce given [Tasks] by [select] types to remove unneeded tasks from the graph.
 * The returned graph will only tasks that are returning selected types, their dependencies and derived dependencies.
 * Additionally this is keeping also the validators for initial state.
 *
 * @return Reduced [Tasks]
 */
operator fun Tasks.invoke(
    select: Set<Parallel.Type<*>>
): Tasks =
    reduceTo(select + initialValidators)

/**
 * Execute tasks in parallel with a given args.
 * Before executing, the [validate] is performed on a [Tasks] for a given [args] to detect missing dependencies.
 *
 * @return [Flow] of [ParallelState] changes.
 */
infix operator fun Tasks.invoke(
    args: ParallelState
): Flow<ParallelState> =
    Execution(validate(args), args).invoke()

// ======================= Types =======================

/**
 * The namespace for parallel execution primitives.
 */
object Parallel {

    /**
     * Abstraction for execution data provider which is also an context for task execution.
     * For initialization purpose some properties are exposed as variable.
     */
    open class Context : Type<Unit> {

        /**
         * Exposed reference to output, accessible by tasks.
         */
        val out: Output by -Logger

        /**
         * The state properties map. Is for initialization purpose, and shouldn't be mutated after initialization.
         */
        internal lateinit var state: ParallelState

        /**
         * Eager property delegate provider and initializer of a state values.
         */
        val eager = EagerProperties { state }

        /**
         * DSL for creating eager delegate accessor to the state value for a given type.
         *
         * @see [EagerProperties.invoke]
         */
        inline operator fun <reified T : Any> Type<T>.not() = eager(this)

        /**
         * DSL for creating lazy delegate accessor to the state value for a given type.
         */
        operator fun <T : Any> Type<T>.unaryMinus() = lazyProperty(this)
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
        val execute: ExecuteTask<R>
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

    data class Event internal constructor(
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

// ======================= Aliases =======================

/**
 * Signature for [Parallel.Task] function.
 */
typealias ExecuteTask<R> = suspend ParallelState.() -> R

/**
 * Common signature for structural log output.
 */
typealias Output = Any.() -> Unit

/**
 * Immutable state for parallel execution.
 */
typealias ParallelState = Map<Parallel.Type<*>, Any>

/**
 * Type for group of parallel tasks. Each task must be unique in group.
 */
typealias Tasks = Set<Parallel.Task<*>>

/**
 * The [Map.values] are representing missing dependencies required by the tasks to provide [Map.keys].
 */
typealias MissingDependencies = Map<Parallel.Type<*>, Set<Parallel.Type<*>>>
