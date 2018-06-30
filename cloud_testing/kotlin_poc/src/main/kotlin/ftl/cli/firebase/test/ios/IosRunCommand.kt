package ftl.cli.firebase.test.ios

import ftl.config.IosConfig
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
        description = ["""Uploads the app and tests to GCS.
Runs the XCTests and XCUITests.
Configuration is read from flank.yml
"""])
class IosRunCommand : Runnable {
    override fun run() {
        val config = IosConfig.load(configPath)
        if (shards > 0) config.testRuns = shards
        runBlocking {
            TestRunner.newRun(config)

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
            IosRunCommand().run()
        }
    }
}
