package flank.corellium.client.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val format = Json {
    ignoreUnknownKeys = true
    encodeDefaults = false
}

@Serializable
data class Instance(
    val id: String = "",
    val name: String = "",
    val key: String = "",
    val flavor: String,
    val type: String = "",
    val project: String,
    val state: String = "",
    val bootOptions: BootOptions = BootOptions(),
    val patches: List<String> = emptyList(),
    val os: String = "",
    val osbuild: String = "",
    val agent: InstanceAgent? = InstanceAgent(),
    val serviceIp: String = "",
    @SerialName("port-adb")
    val portAdb: String = ""
)

@Serializable
data class InstanceAgent(
    val hash: String = "",
    val info: String = ""
)

@Serializable
data class BootOptions(
    val bootArgs: String = "",
    val restoreBootArgs: String = "",
    val udid: String = "",
    val ecid: String = "",
    val screen: String = ""
)

@Serializable
data class Project(
    val id: String,
    val name: String,
    val quotas: Quotas,
    val quotasUsed: Quotas
)

@Serializable
data class Quotas(
    val cores: Int,
    val instances: Int,
    val ram: Int,
    val cpus: Int,
    val gpus: Int? = null,
    val instance: Int? = null
)

@Serializable
data class Id(
    val id: String
)

@Serializable
data class Credentials(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String
)

@Serializable
data class AgentOperation(
    val type: String,
    val op: String,
    val id: Int,
    val path: String = ""
)

@Serializable
data class CommandResult(
    val id: Int,
    val success: Boolean,
    val error: CommandError? = null
) {
    override fun toString() = format.encodeToString(this)
}

@Serializable
data class CommandError(
    val name: String = "",
    val message: String = "",
)
