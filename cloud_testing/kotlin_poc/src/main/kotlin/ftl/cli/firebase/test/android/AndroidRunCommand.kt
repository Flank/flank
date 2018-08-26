package ftl.cli.firebase.test.android

import ftl.args.AndroidArgs
import ftl.run.TestRunner
import kotlinx.coroutines.experimental.runBlocking
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.nio.file.Paths

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
class AndroidRunCommand : Runnable {
    override fun run() {
        val config = AndroidArgs.load(Paths.get(configPath))
        runBlocking {
            TestRunner.newRun(config)
        }
    }

    @Option(names = ["-c", "--config"], description = ["YAML config file path"])
    var configPath: String = "./flank.yml"

    @Option(names = ["-h", "--help"], usageHelp = true, description = ["Prints this help message"])
    var usageHelpRequested: Boolean = false

    companion object {
        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            AndroidRunCommand().run()
        }
    }
}
