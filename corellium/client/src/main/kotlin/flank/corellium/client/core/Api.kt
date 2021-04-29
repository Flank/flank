package flank.corellium.client.core

import flank.corellium.client.agent.Agent
import flank.corellium.client.agent.connectAgent
import flank.corellium.client.console.Console
import flank.corellium.client.console.connectConsole
import flank.corellium.client.data.ConsoleSocket
import flank.corellium.client.data.Id
import flank.corellium.client.data.Instance
import flank.corellium.client.data.Project
import flank.corellium.client.util.withRetry
import io.ktor.client.features.logging.LogLevel
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
import java.io.File
import java.util.UUID

suspend fun Corellium.getAllProjects(): List<Project> =
    getProjectIdList().map {
        withRetry {
            async {
                client.get<Project> {
                    url("$urlBase/projects/$it")
                    header("Authorization", token)
                }
            }
        }
    }.awaitAll()

suspend fun Corellium.getProjectIdList(): List<String> =
    withRetry {
        client.get<List<Id>> {
            url("$urlBase/projects?ids_only=1")
            header("Authorization", token)
        }.map { it.id }
    }

suspend fun Corellium.getProjectInstancesList(
    projectId: String
): List<Instance> =
    withRetry {
        client.get {
            url("$urlBase/projects/$projectId/instances")
            header("Authorization", token)
        }
    }

suspend fun Corellium.createNewInstance(
    newInstance: Instance
): String =
    withRetry {
        client.post<Id> {
            url("$urlBase/instances")
            header("Authorization", token)
            contentType(ContentType.Application.Json)
            body = newInstance
        }.id
    }

suspend fun Corellium.deleteInstance(
    instanceId: String
): Unit =
    withRetry {
        client.delete {
            url("$urlBase/instances/$instanceId")
            header("Authorization", token)
        }
    }

suspend fun Corellium.startInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/start")
            header("Authorization", token)
        }
    }

suspend fun Corellium.stopInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/stop")
            header("Authorization", token)
        }
    }

suspend fun Corellium.pauseInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/stop")
            header("Authorization", token)
        }
    }

suspend fun Corellium.unpauseInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/stop")
            header("Authorization", token)
        }
    }

suspend fun Corellium.getInstanceInfo(
    instanceId: String
): Instance =
    withRetry {
        client.get {
            url("$urlBase/instances/$instanceId")
            header("Authorization", token)
        }
    }

suspend fun Corellium.waitUntilInstanceIsReady(
    instanceId: String
): Unit =
    withRetry {
        while (getInstanceInfo(instanceId).state != "on") {
            // it could takes long time
            // if instance was created just moment ago
            // TODO: adjust the delay time depending on returned state
            delay(20_000)
        }
    }

suspend fun Corellium.connectAgent(
    agentInfo: String
): Agent =
    connectAgent(
        agentUrl = "${urlBase.replace("https", "wss")}/agent/$agentInfo",
        token = token,
        logLevel = LogLevel.NONE,
    )

suspend fun Corellium.getVPNConfig(
    projectId: String,
    type: VPN,
    id: String = UUID.randomUUID().toString()
) {
    val response: HttpResponse = withRetry {
        client.get {
            url("$urlBase/projects/$projectId/vpn-configs/$id.${type.name.toLowerCase()}")
            header("Authorization", token)
        }
    }
    val fileName = if (type == VPN.TBLK) "tblk.zip" else "config.ovpn"
    response.content.copyAndClose(File(fileName).writeChannel())
}

enum class VPN { OVPN, TBLK }

suspend fun Corellium.connectConsole(
    instanceId: String
): Console =
    withRetry {
        val url = client.get<ConsoleSocket> {
            url("$urlBase/instances/$instanceId/console")
            header("Authorization", token)
        }.url
        connectConsole(url, token)
    }
