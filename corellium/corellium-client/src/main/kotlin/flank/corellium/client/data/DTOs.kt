package flank.corellium.client.data

import kotlinx.serialization.Serializable

@Serializable
data class Instance(
    val id: String = "",
    val name: String,
    val key: String = "",
    val flavor: String,
    val type: String = "",
    val project: String,
    val state: String = "",
    val bootOptions: BootOptions = BootOptions(),
    val patches: List<String> = emptyList(),
    val os: String = "",
    val osbuild: String = "",
    val agent: InstanceAgent = InstanceAgent()
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
    val ecid: String = ""
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
