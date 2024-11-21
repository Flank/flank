package flank.execution.linear

import flank.log.Event
import flank.log.Logger
import flank.log.Output
import flank.log.event
import flank.log.normalize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

/**
 * Execute flow of suspendable transformations on a given value.
 *
 * @receiver Value to apply transformations on it.
 * @return Result of transformations.
 */
suspend infix fun <S> S.execute(operations: Flow<Transform<S>>): S =
    operations.fold(this) { value, transform -> transform(value) }

/**
 * Simple parameterized factory for generating [Transform] instances.
 */
class CreateTransformation<S> : (Transform<S>) -> Transform<S> by { it }

/**
 * Type-alias for suspendable transforming operation
 */
typealias Transform<S> = suspend S.() -> S

/**
 * Inject log output to the transform function.
 */
fun <S> Logger.injectLogger(
    type: Any = Unit,
    transform: suspend S.(Output) -> S
): Transform<S> {
    val out: Output = out.normalize { type event it }
    return {
        Event.Start.out()
        val result = transform(this, out)
        Event.Stop.out()
        result
    }
}
