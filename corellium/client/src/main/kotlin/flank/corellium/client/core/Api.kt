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

/**
 * @return A list of [Project] associated with the given account.
 */
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

/**
 * @return A list of project IDs associated with the given credentials.
 */
suspend fun Corellium.getProjectIdList(): List<String> =
    withRetry {
        client.get<List<Id>> {
            url("$urlBase/projects?ids_only=1")
            header("Authorization", token)
        }.map { it.id }
    }

/**
 * @return A list of [Instance] created for the project with the given id.
 */
suspend fun Corellium.getProjectInstancesList(
    projectId: String
): List<Instance> =
    withRetry {
        client.get {
            url("$urlBase/projects/$projectId/instances")
            header("Authorization", token)
        }
    }

/**
 * Creates a new [Instance].
 *
 * A newly created instance needs some time to setup (state == `creating`).
 * Use [waitUntilInstanceIsReady] to ensure VM has `on` state and is ready to use.
 *
 * @param newInstance
 * The following fields for [Instance] are required:
 * * `project` -> id of the project your instance is going to be created in
 * * `name` -> instance's name
 * * `flavor` -> flavor of the instance. Currently supported devices are
listed <a href="https://github.com/corellium/corellium-api#async-createinstanceoptions">here</a>
 * * `os` -> software version
 * @returns Newly created instance id.
 * @see <a href="https://github.com/corellium/corellium-api#async-createinstanceoptions">official api options</a>
 */
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

/**
 * Delete virtual device instance by id.
 *
 * @param instanceId
 */
suspend fun Corellium.deleteInstance(
    instanceId: String
): Unit =
    withRetry {
        client.delete {
            url("$urlBase/instances/$instanceId")
            header("Authorization", token)
        }
    }

/**
 * Start virtual device instance by id.
 *
 * @param instanceId
 */
suspend fun Corellium.startInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/start")
            header("Authorization", token)
        }
    }

/**
 * Stop virtual device instance by id.
 *
 * @param instanceId
 */
suspend fun Corellium.stopInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/stop")
            header("Authorization", token)
        }
    }

/**
 * Pause virtual device instance by id.
 *
 * @param instanceId
 */
suspend fun Corellium.pauseInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/pause")
            header("Authorization", token)
        }
    }

/**
 * Unpause virtual device instance by id.
 *
 * @param instanceId
 */
suspend fun Corellium.unpauseInstance(
    instanceId: String
): Unit =
    withRetry {
        client.post {
            url("$urlBase/instances/$instanceId/unpause")
            header("Authorization", token)
        }
    }

/**
 * Start virtual device instance by id.
 *
 * @param instanceId
 */
suspend fun Corellium.getInstanceInfo(
    instanceId: String
): Instance =
    withRetry {
        client.get {
            url("$urlBase/instances/$instanceId")
            header("Authorization", token)
        }
    }

/**
 * Wait until the instance will change state into `on`.
 *
 * @param instanceId
 */
suspend fun Corellium.waitUntilInstanceIsReady(
    instanceId: String
): Unit =
    withRetry {
        // Possible risk of infinity loop
        while (getInstanceInfo(instanceId).state != Instance.State.ON) {
            // it could takes long time
            // if instance was created just moment ago
            // TODO: adjust the delay time depending on returned state
            delay(20_000)
        }
    }

/**
 * Initialize connection with instance agent.
 *
 * @param agentInfo Can be obtain from [Instance.Agent.info]
 * @return [Agent] connection context
 */
suspend fun Corellium.connectAgent(
    agentInfo: String
): Agent =
    connectAgent(
        agentUrl = "${urlBase.replace("https", "wss")}/agent/$agentInfo",
        token = token,
        logLevel = LogLevel.NONE,
    )

/**
 * Download VPN configuration to connect to the project network.
 * This is only available for cloud.
 * At least one instance must be `on` in the project.
 *
 * The configuration file will be saved as:
 * * `tblk.zip` - [VPN.TBLK]
 * * `config.ovpn` - [VPN.OVPN]
 *
 * @param type Could be either "ovpn" or "tblk" to select between OpenVPN and TunnelBlick configuration formats.
 *                              TunnelBlick files are delivered as a ZIP file and OpenVPN configuration is just a text file.
 * @param clientUUID An arbitrary UUID to uniquely associate this VPN configuration with so it can be later identified
 *                              in a list of connected clients. Optional.
 */
suspend fun Corellium.downloadVPNConfig(
    projectId: String,
    type: VPN,
    clientUUID: String = UUID.randomUUID().toString()
) {
    val response: HttpResponse = withRetry {
        client.get {
            url("$urlBase/projects/$projectId/vpn-configs/$clientUUID.${type.name.lowercase()}")
            header("Authorization", token)
        }
    }
    val fileName = if (type == VPN.TBLK) "tblk.zip" else "config.ovpn"
    response.content.copyAndClose(File(fileName).writeChannel())
}

enum class VPN { OVPN, TBLK }

/**
 * Create console context.
 *
 * @param instanceId Id of instance that serves the console.
 *
 * @return [Console] connection context.
 */
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
