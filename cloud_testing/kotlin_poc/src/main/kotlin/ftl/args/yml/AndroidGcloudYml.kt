package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.Device
import ftl.util.Utils.assertNotEmpty

/**
 * Android specific gcloud parameters
 *
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class AndroidGcloudYmlParams(
        val app: String = "",
        val test: String = "",

        @field:JsonProperty("auto-google-login")
        val autoGoogleLogin: Boolean = true,

        @field:JsonProperty("use-orchestrator")
        val useOrchestrator: Boolean = true,

        @field:JsonProperty("environment-variables")
        val environmentVariables: Map<String, String> = emptyMap(),

        @field:JsonProperty("directories-to-pull")
        val directoriesToPull: List<String> = emptyList(),

        @field:JsonProperty("performance-metrics")
        val performanceMetrics: Boolean = true,

        @field:JsonProperty("test-targets")
        val testTargets: List<String> = emptyList(),

        val device: List<Device> = listOf(Device("NexusLowRes", "28"))
) {
    init {
        assertNotEmpty(app, "app is not set")
        assertNotEmpty(test, "test is not set")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class AndroidGcloudYml(
        val gcloud: AndroidGcloudYmlParams
)
