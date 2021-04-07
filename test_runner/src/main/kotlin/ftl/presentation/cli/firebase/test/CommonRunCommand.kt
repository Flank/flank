package ftl.presentation.cli.firebase.test

import ftl.config.Config
import ftl.config.android.AndroidGcloudConfig
import ftl.config.asDevice
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.common.addDevice
import picocli.CommandLine

abstract class CommonRunCommand : Runnable {

    @CommandLine.Mixin
    private val commonGcloudConfig = CommonGcloudConfig()

    @CommandLine.Mixin
    private val commonFlankConfig = CommonFlankConfig()

    val commonConfig = Config.Partial(
        gcloud = commonGcloudConfig,
        flank = commonFlankConfig
    )

    abstract val config: Config.Platform<*, *>

    @CommandLine.Option(
        names = ["-h", "--help"],
        usageHelp = true,
        description = ["Prints this help message"]
    )
    var usageHelpRequested: Boolean = false

    // Gcloud
    @CommandLine.Option(
        names = ["--device"],
        split = ",",
        description = [
            "A list of DIMENSION=VALUE pairs which specify a target " +
                "device to test against. This flag may be repeated to specify multiple devices. The four device dimensions are: " +
                "model, version, locale, and orientation. If any dimensions are omitted, they will use a default value. Omitting " +
                "all of the preceding dimension-related flags will run tests against a single device using defaults for all four " +
                "device dimensions."
        ]
    )
    fun device(map: Map<String, String>?) {
        config.common.gcloud.addDevice(
            device = map?.asDevice(
                android = config.platform.gcloud is AndroidGcloudConfig
            )
        )
    }

    // Flank debug
    @CommandLine.Option(names = ["--dry"], description = ["Dry run on mock server"])
    var dryRun: Boolean = false

    // Flank specific
    @CommandLine.Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var configPath: String = ""

    @CommandLine.Option(
        names = ["--obfuscate"],
        description = [
            "Replacing internal test names with unique identifiers when using --dump-shards option " +
                "to avoid exposing internal details"
        ]
    )
    var obfuscate: Boolean = false
}
