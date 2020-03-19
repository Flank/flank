package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.cli.firebase.test.CommonRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.mock.MockServer
import ftl.run.common.prettyPrint
import ftl.run.newTestRun
import kotlinx.coroutines.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess

@Command(
    name = "run",
    sortOptions = false,
    headerHeading = "",
    synopsisHeading = "%n",
    descriptionHeading = "%n@|bold,underline Description:|@%n%n",
    parameterListHeading = "%n@|bold,underline Parameters:|@%n",
    optionListHeading = "%n@|bold,underline Options:|@%n",
    header = ["Run tests on Firebase Test Lab"],
    description = ["""Uploads the app and tests to GCS.
Runs the XCTests and XCUITests.
Configuration is read from flank.yml
"""],
    usageHelpAutoWidth = true
)
class IosRunCommand : CommonRunCommand(), Runnable {
    override fun run() {
        if (dryRun) {
            MockServer.start()
        }

        val config = IosArgs.load(Paths.get(configPath), cli = this)

        if (dumpShards) {
            val testShardChunksJson: String = prettyPrint.toJson(config.testShardChunks)
            Files.write(Paths.get(shardFile), testShardChunksJson.toByteArray())
            println("Saved shards to $shardFile")
            exitProcess(0)
        }

        runBlocking {
            newTestRun(config)
        }
    }

    companion object {
        private const val shardFile = "ios_shards.json"
    }

    // Flank debug

    @Option(names = ["--dump-shards"], description = ["Dumps the shards to $shardFile for debugging"])
    var dumpShards: Boolean = false

    // Flank specific

    @Option(
        names = ["-c", "--config"],
        description = ["YAML config file path"]
    )
    var configPath: String = FtlConstants.defaultIosConfig

    // IosGcloudYml.kt

    @Option(
        names = ["--test"],
        description = ["The path to the test package (a zip file containing the iOS app " +
                "and XCTest files). The given path may be in the local filesystem or in Google Cloud Storage using a URL " +
                "beginning with gs://. Note: any .xctestrun file in this zip file will be ignored if --xctestrun-file " +
                "is specified."]
    )
    var test: String? = null

    @Option(
        names = ["--xctestrun-file"],
        description = ["The path to an .xctestrun file that will override any " +
                ".xctestrun file contained in the --test package. Because the .xctestrun file contains environment variables " +
                "along with test methods to run and/or ignore, this can be useful for customizing or sharding test suites. The " +
                "given path may be in the local filesystem or in Google Cloud Storage using a URL beginning with gs://."]
    )
    var xctestrunFile: String? = null

    @Option(
        names = ["--xcode-version"],
        description = ["The version of Xcode that should be used to run an XCTest. " +
                "Defaults to the latest Xcode version supported in Firebase Test Lab. This Xcode version must be supported by " +
                "all iOS versions selected in the test matrix."]
    )
    var xcodeVersion: String? = null

    @Option(
        names = ["--device"],
        split = ",",
        description = ["A list of DIMENSION=VALUE pairs which specify a target " +
                "device to test against. This flag may be repeated to specify multiple devices. The four device dimensions are: " +
                "model, version, locale, and orientation. If any dimensions are omitted, they will use a default value. Omitting " +
                "all of the preceding dimension-related flags will run tests against a single device using defaults for all four " +
                "device dimensions."]
    )
    fun deviceMap(map: Map<String, String>?) {
        if (map.isNullOrEmpty()) return
        val androidDevice = Device(
            model = map.getOrDefault("model", defaultIosModel),
            version = map.getOrDefault("version", defaultIosVersion),
            locale = map.getOrDefault("locale", FtlConstants.defaultLocale),
            orientation = map.getOrDefault("orientation", FtlConstants.defaultOrientation)
        )

        if (device == null) device = mutableListOf()
        device?.add(androidDevice)
    }

    var device: MutableList<Device>? = null

    // IosFlankYml.kt

    @Option(
        names = ["--test-targets"],
        split = ",",
        description = ["A list of one or more test method " +
                "names to run (default: run all test targets)."]
    )
    var testTargets: List<String>? = null
}
