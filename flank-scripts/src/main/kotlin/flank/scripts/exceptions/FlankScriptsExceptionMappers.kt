package flank.scripts.exceptions

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.isClientError
import com.github.kittinunf.result.Result
import flank.scripts.release.hub.GitHubErrorResponse
import flank.scripts.release.updatebugsnag.BugSnagResponse
import flank.scripts.utils.toObject

fun <V : Any, E : FuelError, E2 : Exception> Result<V, E>.mapClientError(transform: (E) -> E2) = when (this) {
    is Result.Success -> Result.Success(value)
    is Result.Failure -> if(error.response.isClientError) Result.Failure(transform(error)) else Result.Failure(error)
}
fun FuelError.toGithubException() =
        GitHubException(response.body().asString("application/json").toObject(GitHubErrorResponse.serializer()))

fun FuelError.toBugsnagException() =
        BugsnagException(response.body().asString("application/json").toObject(BugSnagResponse.serializer()))
