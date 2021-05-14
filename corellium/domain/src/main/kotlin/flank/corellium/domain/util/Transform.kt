package flank.corellium.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.fold

/**
 * Execute flow of suspendable transformations on a given value.
 *
 * @receiver Value to apply transformations on it.
 * @return Result of transformations.
 */
internal suspend infix fun <S> S.execute(operations: Flow<Transform<S>>): S =
    operations.fold(this) { value, transform -> transform(value) }

/**
 * Simple parameterized factory for generating [Transform] instances.
 */
internal class CreateTransformation<S> : (Transform<S>) -> Transform<S> by { it }

/**
 * Type-alias for suspendable transforming operation
 */
private typealias Transform<S> = suspend S.() -> S
