package flank.execution.parallel

import flank.execution.parallel.internal.ContextProvider
import flank.execution.parallel.internal.EagerProperties
import flank.execution.parallel.internal.lazyProperty
import flank.log.Output

// ======================= Types =======================

/**
 * The namespace for parallel execution primitives.
 */
object Parallel {

    /**
     * Abstraction for execution data provider which is also a context for task execution.
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
         * DSL for creating lazy delegate accessor to the state value for a given type with additional property selector.
         */
        protected operator fun <T : Any, V> Type<T>.invoke(select: T.() -> V) = lazyProperty(this, select)

        /**
         * Internal accessor for initializing (validating) eager properties.
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
        val execute: ExecuteTask<R>,
        val expected: Boolean = true,
    ) {
        /**
         * The task signature.
         *
         * @param type A return a type of task.
         * @param args A set of argument types.
         */
        data class Signature<R : Any>(
            val type: Type<R>,
            val args: Set<Type<*>>,
        )
    }

    /**
     * Parameterized factory for creating task functions in scope of [X].
     */
    class Function<X : Context>(override val context: () -> X) : ContextProvider<X>()

    object Logger : Type<Output>

    /**
     * Added to the initial state will force the execution to run parallel tasks one by one.
     */
    object Sequence : Type<Unit>

    object DependenciesError {
        data class Missing(val data: TypeDependencies) : Error("Missing dependencies:")
        data class Duplicate(val data: DuplicateDependencies) : Error("Duplicate dependencies:")
        data class Cycles(val data: List<List<Type<*>>>) : Error("Found cycles in graph:")
    }
}

// ======================= Aliases =======================

/**
 * Signature for [Parallel.Task] function.
 */
typealias ExecuteTask<R> = suspend ParallelState.() -> R

/**
 * Immutable state for parallel execution.
 */
typealias ParallelState = Map<Parallel.Type<*>, Any>

/**
 * Immutable state for parallel execution.
 */
typealias Property = Pair<Parallel.Type<*>, Any>

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
