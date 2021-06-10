package flank.exection.parallel

import flank.exection.parallel.internal.ContextProvider
import flank.exection.parallel.internal.EagerProperties
import flank.exection.parallel.internal.lazyProperty
import java.lang.System.currentTimeMillis

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
        protected val eager = EagerProperties { state }

        /**
         * DSL for creating eager delegate accessor to the state value for a given type.
         *
         * @see [EagerProperties.invoke]
         */
        protected inline operator fun <reified T : Any> Type<T>.not() = eager(this)

        /**
         * DSL for creating lazy delegate accessor to the state value for a given type.
         */
        protected operator fun <T : Any> Type<T>.unaryMinus() = lazyProperty(this)

        /**
         * Internal accessor for initializing (validating) eager properties
         */
        internal fun validate() = eager()
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
         * The task signature.
         *
         * @param type A return type of a task
         * @param args A set of types for arguments
         */
        data class Signature<R : Any>(
            val type: Type<R>,
            val args: Set<Type<*>>,
        )
    }

    /**
     * Parameterized factory for creating task functions in scope of [X].
     */
    class Function<X : Context>(override val context: () -> X) : ContextProvider<X>

    data class Event internal constructor(
        val type: Type<*>,
        val data: Any,
        val timestamp: Long = currentTimeMillis(),
    ) {
        object Start
        object Stop
    }

    object Logger : Type<Output>

    object DependenciesError {
        data class Missing(val data: TypeDependencies) : Error("Missing dependencies $data")
        data class Duplicate(val data: DuplicateDependencies) : Error("Duplicate dependencies $data")
        data class NoEntryPoint(val initial: Set<Type<*>>, val tasks: TypeDependencies) :
            Error("No entry points in tasks $tasks with initial state $initial")

        data class Cycles(val data: List<List<Type<*>>>) : Error("Found cycles in graph $data")
    }
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
typealias TypeDependencies = Map<Parallel.Type<*>, Set<Parallel.Type<*>>>

/**
 * The [Map.values] are representing missing dependencies required by the tasks to provide [Map.keys].
 */
typealias DuplicateDependencies = Map<Parallel.Type<*>, Int>
