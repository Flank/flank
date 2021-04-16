package flank.corellium.api

/**
 * The credentials data required to authorize to corellium API.
 *
 * @property host the host part of base Corellium API url "https://[host]/api/v1".
 */
data class Authorization(
    val host: String,
    val username: String,
    val password: String,
) {

    /**
     * The authorization API call.
     * The successful return makes the rest of API functions available.
     */
    interface Request : flank.corellium.api.Request<Authorization, Unit>
}
