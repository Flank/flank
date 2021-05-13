package flank.corellium.api

import kotlinx.coroutines.Job

/**
 * The scope is representing user authorization in Corellium.
 */
object Authorization {

    /**
     * The credentials data required to authorize to corellium API.
     *
     * @property host the host part of base Corellium API url "https://[host]/api/v1".
     */
    data class Credentials(
        val host: String,
        val username: String,
        val password: String,
    )
    /**
     * The authorization API call.
     * The successful return is making the rest of API functions available.
     */
    fun interface Request : (Credentials) -> Job
}
