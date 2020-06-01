package ftl.config.common

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.ArgsHelper
import ftl.config.Config
import ftl.args.yml.IYmlKeys
import ftl.config.FlankDefaults
import picocli.CommandLine

/**
 * Common Gcloud parameters shared between iOS and Android
 *
 * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
 * https://cloud.google.com/sdk/gcloud/reference/alpha/firebase/test/ios/run
 */
@CommandLine.Command
@JsonIgnoreProperties(ignoreUnknown = true)
data class CommonGcloudConfig @JsonIgnore constructor(
    @JsonIgnore
    override val data: MutableMap<String, Any?>
) : Config {
    @set:CommandLine.Option(
        names = ["--results-bucket"],
        description = ["The name of a Google Cloud Storage bucket where raw test " +
                "results will be stored (default: \"test-lab-<random-UUID>\"). Note that the bucket must be owned by a " +
                "billing-enabled project, and that using a non-default bucket will result in billing charges for the " +
                "storage used."]
    )
    @set:JsonProperty("results-bucket")
    var resultsBucket: String? by data

    @set:CommandLine.Option(
        names = ["--results-dir"],
        description = ["The name of a unique Google Cloud Storage object within the results bucket where raw test results will be " +
                "stored (default: a timestamp with a random suffix). Caution: if specified, this argument must be unique for " +
                "each test matrix you create, otherwise results from multiple test matrices will be overwritten or " +
                "intermingled."]
    )
    @set:JsonProperty("results-dir")
    var resultsDir: String? by data

    @set:CommandLine.Option(
        names = ["--record-video"],
        description = ["Enable video recording during the test. " +
                "Disabled by default."]
    )
    @set:JsonProperty("record-video")
    var recordVideo: Boolean? by data

    @set:CommandLine.Option(
        names = ["--timeout"],
        description = ["The max time this test execution can run before it is cancelled " +
                "(default: 15m). It does not include any time necessary to prepare and clean up the target device. The maximum " +
                "possible testing time is 30m on physical devices and 60m on virtual devices. The TIMEOUT units can be h, m, " +
                "or s. If no unit is given, seconds are assumed. "]
    )
    var timeout: String? by data

    @set:CommandLine.Option(
        names = ["--async"],
        description = ["Invoke a test asynchronously without waiting for test results."]
    )
    var async: Boolean? by data

    @set:CommandLine.Option(
        names = ["--client-details"],
        split = ",",
        description = ["Comma-separated, KEY=VALUE map of additional details to attach to the test matrix." +
                "Arbitrary KEY=VALUE pairs may be attached to a test matrix to provide additional context about the tests being run." +
                "When consuming the test results, such as in Cloud Functions or a CI system," +
                "these details can add additional context such as a link to the corresponding pull request."]
    )
    @set:JsonProperty("client-details")
    var clientDetails: Map<String, String>? by data

    @set:CommandLine.Option(
        names = ["--network-profile"],
        description = ["The name of the network traffic profile, for example --network-profile=LTE, " +
                "which consists of a set of parameters to emulate network conditions when running the test " +
                "(default: no network shaping; see available profiles listed by the `flank test network-profiles list` command). " +
                "This feature only works on physical devices. "]
    )
    @set:JsonProperty("network-profile")
    var networkProfile: String? by data

    @set:CommandLine.Option(
        names = ["--results-history-name"],
        description = ["The history name for your test results " +
                "(an arbitrary string label; default: the application's label from the APK manifest). All tests which use the " +
                "same history name will have their results grouped together in the Firebase console in a time-ordered test " +
                "history list."]
    )
    @set:JsonProperty("results-history-name")
    var resultsHistoryName: String? by data

    @set:CommandLine.Option(
        names = ["--num-flaky-test-attempts"],
        description = ["The number of times a TestExecution should be re-attempted if one or more of its test cases " +
                "fail for any reason. The maximum number of reruns allowed is 10. Default is 0, which implies no reruns."]
    )
    @set:JsonProperty("num-flaky-test-attempts")
    var flakyTestAttempts: Int? by data

    constructor() : this(mutableMapOf())

    companion object : IYmlKeys {
        override val keys = listOf(
            "results-bucket",
            "results-dir",
            "record-video",
            "timeout",
            "async",
            "results-history-name",
            "num-flaky-test-attempts"
        )

        fun default() = CommonGcloudConfig().apply {
            ArgsHelper.yamlMapper.readerFor(CommonGcloudConfig::class.java)

            resultsBucket = ""
            resultsDir = null
            recordVideo = FlankDefaults.DISABLE_VIDEO_RECORDING
            timeout = "15m"
            async = false
            clientDetails = null
            flakyTestAttempts = 0
            networkProfile = null
            resultsHistoryName = null
        }
    }
}
