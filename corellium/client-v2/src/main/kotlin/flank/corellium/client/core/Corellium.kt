package flank.corellium.client.core

import io.ktor.client.HttpClient
import io.ktor.client.features.logging.LogLevel

class Corellium internal constructor(
    internal val client: HttpClient,
    internal val urlBase: String,
    internal val token: String,
    internal val logLevel: LogLevel,
)
