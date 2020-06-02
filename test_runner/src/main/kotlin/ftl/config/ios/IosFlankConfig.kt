package ftl.config.ios

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.yml.IYmlKeys
import ftl.args.yml.IYmlMap
import ftl.config.Config
import picocli.CommandLine

/** Flank specific parameters for iOS */
@CommandLine.Command
@JsonIgnoreProperties(ignoreUnknown = true)
data class IosFlankConfig @JsonIgnore constructor(
    @JsonIgnore
    override val data: MutableMap<String, Any?>
) : Config {
    @set:CommandLine.Option(
        names = ["--test-targets"],
        split = ",",
        description = ["A list of one or more test method " +
                "names to run (default: run all test targets)."]
    )
    @set:JsonProperty("test-targets")
    var testTargets: List<String?>? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys, IYmlMap {

        override val keys = listOf("test-targets")

        override val map = mapOf("flank" to keys)

        fun default() = IosFlankConfig().apply {
            testTargets = emptyList()
        }
    }
}
