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

suspend fun connectCorellium(
    api: String,
    username: String,
    password: String,
    logLevel: LogLevel = LogLevel.NONE
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
            level = logLevel
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
        urlBase = token,
        token = token,
        logLevel = logLevel
    )
}
