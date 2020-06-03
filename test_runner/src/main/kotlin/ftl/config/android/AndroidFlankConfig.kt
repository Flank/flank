package ftl.config.android

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.config.Config
import ftl.args.yml.AppTestPair
import ftl.args.yml.IYmlKeys
import picocli.CommandLine

/** Flank specific parameters for Android */
@JsonIgnoreProperties(ignoreUnknown = true)
data class AndroidFlankConfig @JsonIgnore constructor(
    @JsonIgnore
    override val data: MutableMap<String, Any?>
) : Config {

    @CommandLine.Option(
        names = ["--additional-app-test-apks"],
        split = ",",
        description = ["A list of app & test apks to include in the run. " +
                "Useful for running multiple module tests within a single Flank run."]
    )
    fun additionalAppTestApks(map: Map<String, String>?) {
        if (map.isNullOrEmpty()) return
        if (additionalAppTestApks == null) additionalAppTestApks = mutableListOf()

        val appApk = map["app"]
        val testApk = map["test"]

        if (testApk != null) {
            additionalAppTestApks?.add(
                AppTestPair(
                    app = appApk,
                    test = testApk
                )
            )
        }
    }

    @set:JsonProperty("additional-app-test-apks")
    var additionalAppTestApks: MutableList<AppTestPair>? by data

    @set:CommandLine.Option(
        names = ["--legacy-junit-result"],
        description = ["Fallback for legacy xml junit results parsing."]
    )
    @set:JsonProperty("legacy-junit-result")
    var useLegacyJUnitResult: Boolean? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys {

        override val group = IYmlKeys.Group.FLANK

        override val keys = listOf(
            "additional-app-test-apks",
            "legacy-junit-result"
        )

        fun default() = AndroidFlankConfig().apply {
            additionalAppTestApks = null
            useLegacyJUnitResult = false
        }
    }
}
