package flank.corellium.client

import flank.corellium.client.data.ConsoleSocket
import flank.corellium.client.data.Credentials
import flank.corellium.client.data.Id
import flank.corellium.client.data.Instance
import flank.corellium.client.data.LoginResponse
import flank.corellium.client.data.Project
import flank.corellium.client.logging.LoggingLevel
import flank.corellium.client.util.withProgress
import flank.corellium.client.util.withRetry
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Corellium(
    api: String,
    private val username: String,
    private val password: String,
    private val tokenFallback: String = "",
    private val logging: LoggingLevel = LoggingLevel.None
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
        install(Logging) {
            level = logging.level
        }
    }

    private val urlBase = "https://$api/api/v1"

    private var _token: String? = null

    private val token: String
        get() = _token ?: tokenFallback

    suspend fun logIn(): String {
        _token = withRetry {
            client.post<LoginResponse> {
                url("$urlBase/tokens")
                contentType(ContentType.Application.Json)
                body = Credentials(username, password)
            }.token
        }
        return token
    }

    suspend fun getProjectIdList(): List<String> = withRetry {
        client.get<List<Id>>(
            block = {
                url("$urlBase/projects?ids_only=1")
                contentType(ContentType.Application.Json)
                header("Authorization", token)
            }
        ).map { it.id }
    }

    suspend fun getAllProjects(): List<Project> = getProjectIdList()
        .map {
            withRetry {
                async {
                    client.get<Project> {
                        url("$urlBase/projects/$it")
                        contentType(ContentType.Application.Json)
                        header("Authorization", token)
                    }
                }
            }
        }
        .awaitAll()

    suspend fun getProjectInstancesList(projectId: String): List<Instance> = withRetry {
        client.get {
            url("$urlBase/projects/$projectId/instances")
            contentType(ContentType.Application.Json)
            header("Authorization", token)
        }
    }

    suspend fun createNewInstance(newInstance: Instance): String = withRetry {
        client.post<Id>(
            block = {
                url("$urlBase/instances")
                contentType(ContentType.Application.Json)
                header("Authorization", token)
                body = newInstance
            }
        ).id
    }

    suspend fun deleteInstance(instanceId: String): Unit = withRetry {
        client.delete {
            url("$urlBase/instances/$instanceId")
            contentType(ContentType.Application.Json)
            header("Authorization", token)
        }
    }

    suspend fun getInstanceInfo(instanceId: String): Instance = withRetry {
        client.get {
            url("$urlBase/instances/$instanceId")
            contentType(ContentType.Application.Json)
            header("Authorization", token)
        }
    }

    suspend fun waitUntilInstanceIsReady(instanceId: String) = withProgress {
        while (true) {
            if (getInstanceInfo(instanceId).state == "on") {
                println()
                break
            }
            // it really takes loooong time
            delay(20_000)
        }
    }

    fun createAgent(agentInfo: String): Agent = Agent(
        agentUrl = "${urlBase.replace("https", "wss")}/agent/$agentInfo",
        logging = logging,
        token = token
    )

    suspend fun getVPNConfig(projectId: String, type: VPN, id: String = UUID.randomUUID().toString()) {
        val response: HttpResponse = withRetry {
            client.get {
                url("$urlBase/projects/$projectId/vpn-configs/$id.${type.name.toLowerCase()}")
                header("Authorization", token)
            }
        }
        val fileName = if (type == VPN.TBLK) "tblk.zip" else "config.ovpn"
        response.content.copyAndClose(File(fileName).writeChannel())
    }

    suspend fun getInstanceConsole(instanceId: String) = withRetry {
        val url = client.get<ConsoleSocket> {
            url("$urlBase/instances/$instanceId/console")
            header("Authorization", token)
        }.url
        Console(url, token)
    }
}
