package flank.scripts.exceptions

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.isClientError
import com.github.kittinunf.result.Result
import flank.scripts.utils.toObject

fun <V : Any, E : FuelError, E2 : Exception> Result<V, E>.mapClientError(transform: (E) -> E2) = when (this) {
    is Result.Success -> this
    is Result.Failure -> if (error.response.isClientError) Result.Failure(transform(error)) else this
}

fun <V : Any, E : FuelError> Result<V, E>.mapClientErrorToGithubException() = mapClientError { it.toGithubException() }

fun FuelError.toGithubException() = GitHubException(response.body().asString(APPLICATION_JSON_CONTENT_TYPE).toObject())

fun FuelError.toBugsnagException() =
    BugsnagException(response.body().asString(APPLICATION_JSON_CONTENT_TYPE).toObject())

private const val APPLICATION_JSON_CONTENT_TYPE = "application/json"
