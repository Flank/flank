package flank.corellium.client.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val agent: Agent? = Agent(),
    val serviceIp: String = "",
    @SerialName("port-adb")
    val portAdb: String = ""
) {

    object State {
        const val ON = "on"
        const val OFF = "off"
        const val CREATING = "creating"
        const val DELETING = "deleting"
        const val DELETED = "deleted"
        const val PAUSED = "paused"
        val unavailable = setOf(DELETED, DELETING)
    }

    @Serializable
    data class Agent(
        val hash: String = "",
        val info: String = ""
    )

    /**
     * @param udid Predefined Unique Device ID (UDID) for iOS device
     * @param screen Change the screen metrics for Ranchu devices `XxY[:DPI]`, e.g. `720x1280:280`
     * @param additionalTags features to utilize for the device, valid options include. Check [AdditionalTags]
     */
    @Serializable
    data class BootOptions(
        val bootArgs: String = "",
        val restoreBootArgs: String = "",
        val udid: String = "",
        val ecid: String = "",
        val screen: String = "",
        val additionalTags: List<String> = emptyList(),
    ) {
        /**
         * @property GPU Enable cloud GPU acceleration (Extra costs incurred, cloud only).
         * @property KALLOC Enable kalloc/kfree trace access via GDB (Enterprise only).
         */
        object AdditionalTags {
            const val GPU = "gpu"
            const val KALLOC = "kalloc"
        }
    }
}

@Serializable
data class Project(
    val id: String,
    val name: String,
    val quotas: Quotas,
    val quotasUsed: Quotas
) {

    @Serializable
    data class Quotas(
        val cores: Int,
        val instances: Int,
        val ram: Int,
        val cpus: Int,
        val gpus: Int? = null,
        val instance: Int? = null
    )
}

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
    val error: Error? = null
) {
    @Serializable
    data class Error(
        val name: String = "",
        val message: String = "",
    )
}

@Serializable
data class ConsoleSocket(
    val url: String
)
