package flank.exection.parallel

import flank.exection.parallel.internal.CreateFunction
import flank.exection.parallel.internal.execute
import flank.exection.parallel.internal.lazy
import flank.exection.parallel.internal.subgraph
import java.lang.System.currentTimeMillis

object Parallel {

    interface Context {
        val out: Output
    }

    /**
     * Abstraction for execution data provider which is also an context for step execution.
     * For initialization purpose some properties are exposed as variable
     */
    abstract class Store {
        /**
         * The state properties map. Is for initialization purpose, and shouldn't be mutated after initialization
         */
        internal lateinit var state: ParallelState

        /**
         * Accessor for overridden context output
         */
        val out: Output by OutputType()

        /**
         * Factory method for creating properties from data objects.
         */
        operator fun <T : Any> Type<T>.invoke(): Lazy<T> = lazy(this)
    }

    /**
     * Common interface for the tasks arguments and return values.
     */
    interface Type<T : Any>

    internal object OutputType : Type<Output>

    /**
     * Structure that is representing single task of execution.
     */
    data class Task<C : Context, R : Any>(
        val signature: Signature<R>,
        val execute: C.() -> suspend ParallelState.() -> R
    ) {

        /**
         * The step signature that contains arguments types and returned type.
         */
        class Signature<R : Any>(
            val args: Set<Type<*>>,
            val returns: Type<R>,
        )
    }

    /**
     * Parameterized factory for creating step functions in scope of [S]
     */
    class Function<S : Store>(override val store: () -> S) : CreateFunction<S>

    data class Event(
        val type: Type<*>,
        val data: Any,
        val timestamp: Long = currentTimeMillis(),
    ) {
        object Start
        object Stop
    }
}

typealias Output = Any.() -> Unit

typealias ParallelState = Map<Parallel.Type<*>, Any>

typealias Tasks<C> = Set<Parallel.Task<C, *>>

/**
 * Factory function for creating [Parallel.Task.Signature] from expected type and argument types.
 */
infix fun <T : Any> Parallel.Type<T>.from(args: Set<Parallel.Type<*>>) =
    Parallel.Task.Signature(args, this)

/**
 * Factory function for creating [Parallel.Task] from signature and execute function
 */
infix fun <C : Parallel.Context, R : Any> Parallel.Task.Signature<R>.using(execute: C.() -> suspend ParallelState.() -> R) =
    Parallel.Task(this, execute)

/**
 * Factory function for creating [Parallel.Task] directly from expected type without specifying required arguments.
 */
infix fun <C : Parallel.Context, R : Any> Parallel.Type<R>.using(execute: C.() -> suspend ParallelState.() -> R) =
    Parallel.Task.Signature(emptySet(), this).using(execute)

/**
 * Execute tasks on a given state
 */
suspend fun <C : Parallel.Context> C.execute(tasks: Tasks<C>, select: Tasks<C> = emptySet(), state: ParallelState = emptyMap()) =
    execute(context = this, state, tasks.subgraph(select))
