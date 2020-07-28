package flank.scripts.release.hub

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import flank.scripts.exceptions.mapClientError
import flank.scripts.exceptions.toGithubException

fun removeOldTag(tag: String, username: String, password: String) =
        Fuel.delete(REMOVE_ENDPOINT + tag)
                .authentication()
                .basic(username, password)
                .response()
                .third
                .mapClientError { it.toGithubException() }

private const val REMOVE_ENDPOINT = "https://api.github.com/repos/Flank/flank/git/refs/tags/"
