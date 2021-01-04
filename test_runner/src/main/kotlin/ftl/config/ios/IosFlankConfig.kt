package ftl.config.ios

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ftl.args.yml.IYmlKeys
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
        description = [
            "A list of one or more test method " +
                "names to run (default: run all test targets)."
        ]
    )
    @set:JsonProperty("test-targets")
    var testTargets: List<String?>? by data

    @set:CommandLine.Option(
        names = ["--only-test-configuration"],
        description = [
            "Constrains a test action to only test a specified test configuration within a test plan " +
                    "and exclude all other test configurations. (default: run all test configurations)." +
                    "Flank can  combine  multiple  constraint options, but -only-test-configuration has precedence over -skip-test-configuration. " +
                    "Each test configuration name must match the name of a configuration specified in a test plan and is case-sensitive."
        ]
    )
    @set:JsonProperty("only-test-configuration")
    var onlyTestConfiguration: String? by data

    @set:CommandLine.Option(
        names = ["--skip-test-configuration"],
        description = [
            "-skip-test-configuration constrains a test action to skip a specified test configuration " +
                    "and include all other test configurations. (default: run all test configurations). " +
                    "Flank can combine  multiple constraint options, but -only-test-configuration has precedence over -skip-test-configuration. " +
                    "Each test configuration name must match the name of a configuration specified in a test plan and is case-sensitive."
        ]
    )
    @set:JsonProperty("skip-test-configuration")
    var skipTestConfiguration: String? by data

    constructor() : this(mutableMapOf<String, Any?>().withDefault { null })

    companion object : IYmlKeys {

        override val group = IYmlKeys.Group.FLANK

        override val keys = listOf("test-targets", "only-test-configuration", "skip-test-configuration")

        fun default() = IosFlankConfig().apply {
            testTargets = emptyList()
            onlyTestConfiguration = null
            skipTestConfiguration = null
        }
    }
}
