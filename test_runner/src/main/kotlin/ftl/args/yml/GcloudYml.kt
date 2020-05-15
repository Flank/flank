package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.FlankDefaults

/**
 * Common Gcloud parameters shared between iOS and Android
 *
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 * https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class GcloudYmlParams(
    @field:JsonProperty("results-bucket")
    var resultsBucket: String = "",

    @field:JsonProperty("results-dir")
    var resultsDir: String? = null,

    @field:JsonProperty("record-video")
    val recordVideo: Boolean = FlankDefaults.DISABLE_VIDEO_RECORDING,

    val timeout: String = "15m",

    val async: Boolean = false,

    @field:JsonProperty("client-details")
    val clientDetails: Map<String, String>? = null,

    @field:JsonProperty("network-profile")
    val networkProfile: String? = null,

    @field:JsonProperty("results-history-name")
    val resultsHistoryName: String? = null,

    @field:JsonProperty("num-flaky-test-attempts")
    val flakyTestAttempts: Int = 0
) {
    companion object : IYmlKeys {
        override val keys =
            listOf("results-bucket", "results-dir", "record-video", "timeout", "async",
                "results-history-name", "num-flaky-test-attempts")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class GcloudYml(
    val gcloud: GcloudYmlParams = GcloudYmlParams()
) {
    companion object : IYmlMap {
        override val map = mapOf("gcloud" to GcloudYmlParams.keys)
    }
}
