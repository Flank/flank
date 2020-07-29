package flank.scripts.release.hub

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toGithubException

fun deleteOldTag(tag: String, username: String, password: String) =
        Fuel.delete(DELETE_ENDPOINT + tag)
                .authentication()
                .basic(username, password)
                .response()
                .third
                .mapClientError { it.toGithubException() }

private const val DELETE_ENDPOINT = "https://api.github.com/repos/Flank/flank/git/refs/tags/"
