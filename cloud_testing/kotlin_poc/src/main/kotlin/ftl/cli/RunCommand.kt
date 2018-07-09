package ftl.cli

import ftl.android.*
import ftl.config.YamlConfig
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option

@Command(name = "run",
        sortOptions = false,
        headerHeading = "",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n",
        header = ["Run tests on Firebase Test Lab"],
        description = ["""Uploads the app and test apk to GCS.
Runs the espresso tests using orchestrator.
Configuration is read from flank.yml
"""])
class RunCommand : Runnable {
    override fun run() {
        val config = YamlConfig.load(configPath)
        if (shards > 0) config.testRuns = shards

        runBlocking {
            // Verify each device config
            config.devices.forEach { device ->
                val deviceConfigTest = AndroidCatalog.init(config.projectId).supportedDeviceConfig(device.model, device.version)
                when (deviceConfigTest) {
                    SupportedDeviceConfig -> TestRunner.newRun(config)
                    UnsupportedModelId -> throw RuntimeException("Unsupported model id, '${device.model}'\nSupported model ids: ${AndroidCatalog.androidModelIds}")
                    UnsupportedVersionId -> throw RuntimeException("Unsupported version id, '${device.version}'\nSupported Version ids: ${AndroidCatalog.androidVersionIds}")
                    is IncompatibleModelVersion -> throw RuntimeException("Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': ${deviceConfigTest}")
                }
            }

        }
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = "./flank.yml"

    @Option(names = ["-s", "--shards"], description = ["Amount of shards to use"])
    var shards: Int = -1

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            RunCommand().run()
        }
    }
}
