package flank.corellium.client.core

import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope

class Corellium internal constructor(
    internal val client: HttpClient,
    internal val urlBase: String,
    internal val token: String
) : CoroutineScope by client
