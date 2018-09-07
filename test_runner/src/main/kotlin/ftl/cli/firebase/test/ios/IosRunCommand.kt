package ftl.cli.firebase.test.ios

import ftl.args.IosArgs
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Paths

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
"""]
)
class IosRunCommand : Runnable {
    override fun run() {
        val config = IosArgs.load(Paths.get(configPath))
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = "./flank.ios.yml"

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false
}
