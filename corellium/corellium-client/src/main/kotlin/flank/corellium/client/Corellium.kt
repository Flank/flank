package flank.corellium.client

import flank.corellium.client.data.Credentials
import flank.corellium.client.data.Id
import flank.corellium.client.data.Instance
import flank.corellium.client.data.LoginResponse
import flank.corellium.client.data.Project
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.ClientWebSocketSession
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json

class Corellium(
    private val api: String,
    private val username: String,
    private val password: String,
    private val tokenFallback: String = ""
) {

    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(
                Json {
                    ignoreUnknownKeys = true
                    encodeDefaults = false
                }
            )
        }
    }

    private val wsClient = HttpClient {
        install(WebSockets)
    }

    private val urlBase = "https://api/api/v1"

    private var _token: String? = null

    private val token: String
        get() = _token ?: tokenFallback

    suspend fun logIn() {
        _token = client.post<LoginResponse>() {
            url("$urlBase/tokens")
            contentType(ContentType.Application.Json)
            body = Credentials(username, password)
        }.token
    }

    suspend fun getProjectIdList(): List<Id> = client.get() {
        url("$urlBase/projects?ids_only=1")
        contentType(ContentType.Application.Json)
        header("Authorization", token)
    }

    suspend fun getAllProjects(): List<Project> = coroutineScope {
        getProjectIdList()
            .map {
                async {
                    client.get<Project>() {
                        url("$urlBase/projects/${it.id}")
                        contentType(ContentType.Application.Json)
                        header("Authorization", token)
                    }
                }
            }
            .awaitAll()
    }

    suspend fun getProjectInstancesList(projectId: String): List<Instance> = client.get() {
        url("$urlBase/projects/$projectId/instances")
        contentType(ContentType.Application.Json)
        header("Authorization", token)
    }

    suspend fun createNewInstance(newInstance: Instance): Id = client.post() {
        url("$urlBase/instances")
        contentType(ContentType.Application.Json)
        header("Authorization", token)
        body = newInstance
    }

    suspend fun deleteInstance(instanceId: String): Unit = client.delete() {
        url("$urlBase/instances/$instanceId")
        contentType(ContentType.Application.Json)
        header("Authorization", token)
    }

    suspend fun getInstanceInfo(instanceId: String): Instance = client.get() {
        url("$urlBase/instances/$instanceId")
        contentType(ContentType.Application.Json)
        header("Authorization", token)
    }

    suspend fun createAgent(agentInfo: String) {
        Agent(
            wsClient.webSocketSession() {
                url("$urlBase/agent/$agentInfo")
                contentType(ContentType.Application.Json)
                header("Authorization", token)
            }
        )
    }
}

class Agent(
    private val connection: ClientWebSocketSession
)
