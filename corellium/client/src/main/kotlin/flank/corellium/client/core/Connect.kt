package flank.corellium.client.core

import flank.corellium.client.data.Credentials
import flank.corellium.client.data.LoginResponse
import flank.corellium.client.util.withRetry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

/**
 * Factory method for creating initialized Corellium connection context.
 *
 * @param api Corellium host address without protocol (https), example: `yourcompany.enterprise.corellium.com`.
 * @param username Corellium account user name.
 * @param password Corellium account password.
 * @return Context of initialized Corellium connection.
 */
suspend fun connectCorellium(
    api: String,
    username: String,
    password: String
): Corellium {
    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                }
            )
        }
        install(Logging) {
            level = LogLevel.NONE
        }
    }

    val urlBase = "https://$api/api/v1"

    val token = withRetry {
        client.post<LoginResponse> {
            url("$urlBase/tokens")
            contentType(ContentType.Application.Json)
            body = Credentials(username, password)
        }.token
    }

    return Corellium(
        client = client,
        urlBase = urlBase,
        token = token
    )
}
