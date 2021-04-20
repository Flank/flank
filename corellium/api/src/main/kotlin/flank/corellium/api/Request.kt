package flank.corellium.api

/**
 * Abstraction for the API request function.
 * @param A the argument of the [Request]
 * @param R the result of the [Request]
 */
interface Request<A, R> {
    suspend operator fun A.invoke(): R
}

suspend infix operator fun <A, R> Request<A, R>.invoke(data: A) = run { data() }
